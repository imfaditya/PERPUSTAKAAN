import java.io.*;
import java.security.spec.RSAOtherPrimeInfo;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        String pilihanUser;
        boolean isLanjutkan = true;
        Scanner terminalInput = new Scanner(System.in);

        // Main Menu
        while (isLanjutkan) {
            System.out.println("\nPerpustakaan");
            System.out.println("1. Tambah Data Buku");
            System.out.println("2. Tampilkan Data Buku");
            System.out.println("3. Cari Data Buku");
            System.out.println("4. Hapus Data Buku");
            System.out.println("5. Ubah Data Buku");
            System.out.println("========================");
            System.out.print("Pilihan Anda : ");
            pilihanUser = terminalInput.next();
            System.out.println();

            switch (pilihanUser) {
                case "1":
                    System.out.println("Tambah Data Buku");
                    tambahDataBuku();
                    break;
                case "2":
                    System.out.println("Tampilkan Data Buku");
                    tampilkanDataBuku();
                    break;
                case "3":
                    System.out.println("Cari Data Buku");
                    cariDataBuku();
                    break;
                case "4":
                    System.out.println("Hapus Data Buku");
                    hapusDataBuku();
                    break;
                case "5":
                    System.out.println("Ubah Data Buku");
                    ubahDataBuku();
                default:
                    System.err.print("Pilihan Anda Tidak Ada di Menu");
                    System.out.println();
                    break;
            }
            isLanjutkan = getYesOrNo("Jalankan Ulang Aplikasi ?");
        }
    }

    private static void tampilkanDataBuku() throws IOException{
        FileReader inputReader;
        BufferedReader inputBuffer;

        // Baca File dan Masukan kedalam Buffer
        try {
            inputReader = new FileReader("database.txt");
            inputBuffer = new BufferedReader(inputReader);
        } catch (IOException e){
            System.err.println("Database / File Tidak Ditemukan");
            // Ketika error / database tidak ditemukan, maka aplikasi akan keluar dari function ini
            // Sehingga inputBuffer yang ada dibawah dianggap tidak akan pernah null
            // Karena jika null / tidak ditemukan, program akan terlebih dahulu dari function sebelum dilakukan readLine
            return;
        }

        // Masukan Buffer ke tokenizer dan tampilkan di layar
        int noData = 0;
        String dataBuffer = inputBuffer.readLine();
        // Ketika EOF dicapai, maka akan mengembalikan nilai null
        // Delimiter = pemisah antara kata di dalam datbaase
        System.out.printf("| %2s | %-20s | %-20s | %-20s |\n", "No", "Tahun", "Penulis", "Judul Buku");

        while (dataBuffer != null){
            StringTokenizer tokenizer = new StringTokenizer(dataBuffer, ",");

            noData++;
            System.out.printf("| %2d |", noData);
            // Skip baca kata pertama disetiap line nya karena merupakan primary key yang tidak akan ditampilkan
            tokenizer.nextToken();
            while (tokenizer.hasMoreTokens()){
                System.out.printf(" %-20s |", tokenizer.nextToken());
            }
            System.out.println();

            dataBuffer = inputBuffer.readLine();

        }


    }

    private static void cariDataBuku() throws IOException{
        Scanner terminalInput = new Scanner(System.in);

        // Cek apakah file database sudah ada
        File fDatabase = new File("database.txt");
        if (!fDatabase.exists()){
            return;
        }

        // Baca inputan keyword pencarian dari user
        System.out.print("Masukan Kata Kunci Pencarian : ");
        String keyword = terminalInput.nextLine();

        // Ubah inputan user kedalam bentuk array
        StringTokenizer tokenizer = new StringTokenizer(keyword, " ");
        int panjangTokenizer = tokenizer.countTokens();
        String[] keywords = new String[panjangTokenizer];
        for (int i = 0; i < panjangTokenizer; i++){
            keywords[i] = tokenizer.nextToken();
        }

        // Ubah inputan user kedalam bentuk array menggunakan split
        String[] keywords2 = keyword.split("\\s");

        // Cek keywords dengan database
        cekBukuDiDatabase(keywords, true);
    }

    private static void tambahDataBuku() throws IOException{
        FileWriter outputWriter;
        BufferedWriter outputBuffer;
        Scanner terminalInput = new Scanner(System.in);
        String tahun, penulis, judul;

        // Baca Inputan User
        System.out.println("Input Buku");
        System.out.print("Masukan Tahun Terbit (YYYY) : ");
        tahun = getFormatTahun();
        System.out.print("Masukan Nama Penulis : ");
        penulis = terminalInput.nextLine();
        System.out.print("Masukan Judul Buku   : ");
        judul = terminalInput.nextLine();


        // Masukan kedalam format array agar bisa di cek apakah sudah ada atau belum data tersebut di database
        String[] keywords = {tahun, judul, penulis};
        boolean isExist = cekBukuDiDatabase(keywords, false);

        if (!isExist){
            boolean isWrite = getYesOrNo("Apakah Yakin Data akan Ditambahkan ke Database?");
        }

        // Generate Primary Key
        // format PK yg digunakan : namapenulistanpaspasi_tahunterbit_nobukupadatahuntersebutdarisipenulis

        long noEntri = getNoEntriBukuPertahun(penulis, tahun);
        String primaryKey = (penulis.replaceAll("\\s+", "").toLowerCase() + "_" + tahun + "_" + (noEntri+1));

        if (isExist){
            System.out.println("\nBuku Yang Akan Anda Input Telah Ada di Database\nDengan Data Sebagai Berikut :");
            cekBukuDiDatabase(keywords, true);
        } else{
            boolean isWrite = getYesOrNo("Yakin Data Akan Ditambahkan?");
            if (isWrite) {
                // Parameter append digunakan agar saat menulis file tidak overwrite, melainkan menambah
                outputWriter = new FileWriter("database.txt", true);
                outputBuffer = new BufferedWriter(outputWriter);
                outputBuffer.write(primaryKey + "," + tahun + "," + penulis + "," + judul);
                outputBuffer.newLine();
                outputBuffer.flush();
                outputBuffer.close();
            }
        }
    }

    private static void hapusDataBuku() throws IOException{
        // Baca file database
        File fileDatabase = new File("database.txt");
        FileReader inputReader = new FileReader(fileDatabase);
        BufferedReader inputBuffer = new BufferedReader(inputReader);

        // Buat file temporary
        File fileTemporary = new File("tempDatabase.txt");
        FileWriter outputWriter = new FileWriter(fileTemporary);
        BufferedWriter outputBuffer = new BufferedWriter(outputWriter);

        // Tampilkan list data
        tampilkanDataBuku();

        // Get input urutan data yang akan dihapus
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\nMasukan No Entry Data Yang Akan Dihapus : ");
        int entryHapus = terminalInput.nextInt();

        // Baca file input per line
        String data = inputBuffer.readLine();
        int hitungData = 0;


        while(data != null){
            hitungData++;
            boolean isDelete = false;
            StringTokenizer tokenizer = new StringTokenizer(data, ",");

            if(hitungData == entryHapus){
                System.out.println("\nData Yang Akan Di Hapus : ");
                System.out.println("-------------------------");
                System.out.println("Primary Key  : " + tokenizer.nextToken());
                System.out.println("Tahun Terbit : " + tokenizer.nextToken());
                System.out.println("Nama Penulis : " + tokenizer.nextToken());
                System.out.println("Judul Buku   : " + tokenizer.nextToken());
                System.out.println("-------------------------");

                isDelete = getYesOrNo("Yakin Data Diatas Akan Dihapus ?");
            }

            if (isDelete){
                System.out.println("Data Berhasil Dihapus\n");
            } else {
                outputBuffer.write(data);
                outputBuffer.newLine();
            }

            data = inputBuffer.readLine();
        }
        outputBuffer.flush();

        outputBuffer.close();
        outputWriter.close();
        inputBuffer.close();
        inputReader.close();

        inputReader = new FileReader(fileTemporary);
        inputBuffer = new BufferedReader(inputReader);

        outputWriter = new FileWriter(fileDatabase);
        outputBuffer = new BufferedWriter(outputWriter);

        data = inputBuffer.readLine();

        while(data != null){
            outputBuffer.write(data);
            outputBuffer.newLine();
            data = inputBuffer.readLine();
        }

        outputBuffer.flush();

        outputBuffer.close();
        outputWriter.close();
        inputBuffer.close();
        inputReader.close();

        fileTemporary.deleteOnExit();

    }

    private static void ubahDataBuku() throws IOException{
        Scanner terminalInput = new Scanner(System.in);

        // Baca file database
        File fileDatabase = new File("database.txt");
        FileReader inputReader = new FileReader(fileDatabase);
        BufferedReader inputBuffer = new BufferedReader(inputReader);

        // Buat file temporary
        File fileTemporary = new File("tempDatabase.txt");
        FileWriter outputWriter = new FileWriter(fileTemporary);
        BufferedWriter outputBuffer = new BufferedWriter(outputWriter);

        // Tampilkan list data
        tampilkanDataBuku();

        // Ambil input user, data mana yang akan diubah
        System.out.print("Masukan No Entry Yang Akan Diubah : ");
        int entryUbah = terminalInput.nextInt();

        // Baca file input per line
        String data = inputBuffer.readLine();

        int count = 0;

        while (data != null){
            StringTokenizer tokenizer = new StringTokenizer(data, ",");
            count++;
            if(entryUbah == count){
                System.out.println("\nData Yang Akan Di Ubah : ");
                System.out.println("-------------------------");
                System.out.println("Primary Key  : " + tokenizer.nextToken());
                System.out.println("Tahun Terbit : " + tokenizer.nextToken());
                System.out.println("Nama Penulis : " + tokenizer.nextToken());
                System.out.println("Judul Buku   : " + tokenizer.nextToken());
                System.out.println("-------------------------");

                boolean isUbah = getYesOrNo("Yakin Untuk Diubah?");

                // Ubah data
                if (isUbah){
                    tokenizer = new StringTokenizer(data, ",");
                    String[] dataLabel = {"tahun", "penulis", "judul"};
                    String[] dataTemp = new String[3];

                    String dataOriginal = tokenizer.nextToken();
                    // Masukan tokenizer pertama ke string untuk mengambil primary key
                    String primaryKey = dataOriginal;

                    for (int i = 0; i < dataLabel.length; i++){
                        dataOriginal = tokenizer.nextToken();
                        boolean isFieldUbah = getYesOrNo("Apakah" + dataLabel[i] + " Akan Dirubah? : ");

                        if(isFieldUbah){
                            System.out.print("Masukan Data " + dataLabel[i] + " Baru : ");
                            if (dataLabel.equals("tahun")){
                                dataTemp[i] = getFormatTahun();
                            } else {
                                dataTemp[i] = terminalInput.next();
                            }
                        }else{
                            dataTemp[i] = dataOriginal;
                        }
                    }

                    // Pastikan apakah data yang akan dirubah sudah benar
                    tokenizer = new StringTokenizer(data, ",");
                    System.out.println("\nKonfirmasi Data Yang Akan Di Ubah : ");
                    System.out.println("--------------------------------------");
                    System.out.println("Primary Key  : " + tokenizer.nextToken());
                    System.out.println("Tahun Terbit : " + tokenizer.nextToken() + " --> " + dataTemp[0]);
                    System.out.println("Nama Penulis : " + tokenizer.nextToken() + " --> " + dataTemp[1]);
                    System.out.println("Judul Buku   : " + tokenizer.nextToken() + " --> " + dataTemp[2]);
                    System.out.println("--------------------------------------");

                    boolean isUpdate = getYesOrNo("Apakah data diatas sudah sesuai untuk dirubah ? (y/n) : ");

                    if (isUpdate){
                        boolean isExist = cekBukuDiDatabase(dataTemp, false);
                        if (isExist){
                            System.err.println("Data buku sudah da di database, proses update dibatalkan!");
                            outputBuffer.write(data);
                            outputBuffer.newLine();
                        } else{
                            outputBuffer.write(primaryKey + "," + dataTemp[0] + "," + dataTemp[1] + "," + dataTemp[2]);
                            outputBuffer.newLine();
                        }
                    }
                }
            }else{
                // Copy data ke temp
                outputBuffer.write(data);
                outputBuffer.newLine();
            }

            data = inputBuffer.readLine();
        }

        // Commit
        outputBuffer.flush();

        outputBuffer.close();
        outputWriter.close();
        inputBuffer.close();
        inputReader.close();

        inputReader = new FileReader(fileTemporary);
        inputBuffer = new BufferedReader(inputReader);

        outputWriter = new FileWriter(fileDatabase);
        outputBuffer = new BufferedWriter(outputWriter);

        data = inputBuffer.readLine();

        while(data != null){
            outputBuffer.write(data);
            outputBuffer.newLine();
            data = inputBuffer.readLine();
        }

        outputBuffer.flush();

        outputBuffer.close();
        outputWriter.close();
        inputBuffer.close();
        inputReader.close();

        fileTemporary.deleteOnExit();
    }

    private static boolean cekBukuDiDatabase(String[] keywords, boolean isDisplay) throws IOException{
        // Baca file dan buffer
        FileReader inputReader;
        BufferedReader inputBuffer;
        boolean isExist = false;

        try {
            inputReader = new FileReader("database.txt");
            inputBuffer = new BufferedReader(inputReader);
        } catch (IOException e){
            System.err.println("Database Tidak Ditemukan");
            return isExist;
        }

        // Cek keywords dengan data di dabase

        // Ambil satu baris data pada database
        String data = inputBuffer.readLine();

        int noData = 0;

        while (data != null){
            isExist = true;

            // Cek apakah baris yang diambil mengandung keyword
            for (String keyword:keywords){
                isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
            }



            // Jika isExist true dan isDisplay true maka akan ditampilkan
            // Jika isExist false maka akan langsung lanjut pengecekan baris selanjutnya
            // Jika isExist true dan isDisplay false maka akan keluar dari perulangan -- Karena hanya untuk mengecek maka 1 baris data yang true keseluruhan saja sudah cukup
            if (isExist){
                if (isDisplay){
                    StringTokenizer tokenizer = new StringTokenizer(data,",");
                    try {
                        tokenizer.nextToken();
                    } catch (NoSuchElementException e){
                        System.out.println("masok");
                        break;
                    }

                    noData++;

                    if (noData == 1){
                        System.out.printf("| %2s | %-20s | %-20s | %-20s |\n", "No", "Tahun", "Penulis", "Judul Buku");
                    }

                    System.out.printf("| %-2d |", noData);
                    while (tokenizer.hasMoreTokens()){
                        System.out.printf(" %-20s |", tokenizer.nextToken());
                    }
                    System.out.println();
                } else {
                    break;
                }
            }

            data = inputBuffer.readLine();
        }
        return isExist;
    }

    private static boolean getYesOrNo(String message){
        Scanner terminalInput = new Scanner(System.in);
        System.out.print(message + " (y/n) ");
        String pilihanUser = terminalInput.next();

        while (!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n")){
            System.err.println("Pilihan anda bukan y atau n");
            System.out.print("\n"+message+" (y/n)? ");
            pilihanUser = terminalInput.next();
        }

        boolean yesOrNo = pilihanUser.equalsIgnoreCase("y");
        return yesOrNo;

    }
    private static String getFormatTahun(){
        Scanner terminalInput = new Scanner(System.in);
        String tahun = terminalInput.nextLine();
        boolean formatTahun = false;

        while (!formatTahun){
            try {
                Year.parse(tahun, DateTimeFormatter.ofPattern("yyyy"));
                formatTahun = true;
            } catch (Exception e){
                System.out.print("Format Tahun Yang Dimasukan Salah \nMasukan Format Tahun (YYYY) : ");
                tahun = terminalInput.nextLine();
            }
        }

        return tahun;
    }
    private static long getNoEntriBukuPertahun(String penulis, String tahun) throws IOException{
        // Baca database
        FileReader inputReader = new FileReader("database.txt");
        BufferedReader inputBuffer = new BufferedReader(inputReader);
        String data, primaryKey;
        long hitung = 0;
        penulis = penulis.replaceAll("\\s+", "");

        data = inputBuffer.readLine();
        while (data != null){

            // Scanner membaca satu baris line data dari input buffer
            Scanner bacaData = new Scanner(data);
            bacaData.useDelimiter(",");


            // Memasukan data primary key dari scanner kedalam variable string
            primaryKey = bacaData.next();
            bacaData = new Scanner(primaryKey);
            bacaData.useDelimiter("_");


            if ((bacaData.next().equalsIgnoreCase(penulis)) && (bacaData.next().equalsIgnoreCase(tahun))){
                hitung++;
            }



            data = inputBuffer.readLine();

        }

        return hitung;
    }
}





















