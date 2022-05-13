import java.io.*;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.Scanner;
import java.util.StringTokenizer;

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
            System.out.println("3. Ubah Data Buku");
            System.out.println("4. Hapus Data Buku");
            System.out.println("========================");
            System.out.print("Pilihan Anda : ");
            pilihanUser = terminalInput.next();
            System.out.println();

            switch (pilihanUser) {
                case "1":
                    System.out.println("Tambah Data Buku");
                    break;
                case "2":
                    System.out.println("Tampilkan Data Buku");
                    tampilkanDataBuku();
                    break;
                case "3":
                    System.out.println("Ubah Data Buku");
                    break;
                case "4":
                    System.out.println("Hapus Data Buku");
                    break;
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
            System.err.println(e);
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
            // Skip baca kata pertama disetiap line nya
            tokenizer.nextToken();
            while (tokenizer.hasMoreTokens()){
                System.out.printf(" %-20s |", tokenizer.nextToken());
            }
            System.out.println();

            dataBuffer = inputBuffer.readLine();

        }


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
}
