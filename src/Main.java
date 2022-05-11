import java.io.IOException;
import java.util.Scanner;

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
