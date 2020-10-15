package encryptdecrypt;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

class Cryption {
    private String line = "";
    private String mode = "enc";
    private String alg = "shift";

    private int count = 0;

    protected AlgorithmCrypt crt;

    private String res = "";
    private String out = "";
    private String in = "";

    private File fileIn;
    private File fileOut;

    public Cryption (String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-out":
                    out = args[i + 1];
                    fileOut = new File(out);
                    break;
                case "-in":
                    if (line.isEmpty()) {
                        in = args[i + 1];
                        fileIn = new File(in);
                    }
                    break;
                case "-data":
                    line = args[i + 1];
                    break;
                case "-key":
                    count = Integer.parseInt(args[i + 1]);
                    break;
                case "-mode":
                    mode = args[i + 1];
                    break;
                case "-alg":
                    alg = args[i + 1];
                    break;
                default:
                    break;
            }
        }
    }

    protected AlgorithmCrypt setAlgorithm() {
        switch (alg) {
            case "shift":
                return new ShiftAlgorithmCrypt();
            default:
                return new UnicodeAlgorithmCrypt();
        }
    }

    protected String getAlgorithm() {
        return crt.alg(mode, line, count);
    }

    protected void getInput() {
        if (line.isEmpty()) {
            try (Scanner scanner = new Scanner(fileIn)) {
                while (scanner.hasNext()) {
                    line += scanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    protected void getOutput() {
        if (!out.isEmpty()) {
            try (FileWriter writer = new FileWriter(fileOut)) {
                writer.write(res);
            } catch (IOException e) {
                System.out.println("Error");
            }
        } else {
            System.out.print(res);
        }
    }

    public void cryption() {
        getInput();

        crt = setAlgorithm();

        res = getAlgorithm();
        System.out.println(res);
        getOutput();
    }
}

interface AlgorithmCrypt {
    String alg(String mode, String line, int count);
}

class UnicodeAlgorithmCrypt implements AlgorithmCrypt {
    @Override
    public String alg (String mode, String line, int count) {
        AlgorithmCrypt crt = null;
        if (mode == "enc") {
            crt = new EncUnicodeAlgorithmCrypt();
        } else  {
            crt = new DecUnicodeAlgorithmCrypt();
        }

        return crt.alg(mode, line, count);

    }
}

class EncUnicodeAlgorithmCrypt extends ShiftAlgorithmCrypt {
    @Override
    public String alg (String mode, String line, int count) {
        mode = "";
        for (int i = 0; i < line.length(); i++) {
            char ch = (char)(line.charAt(i));
            ch += count;
            mode += ch;
        }
        return mode;
    }
}

class DecUnicodeAlgorithmCrypt extends ShiftAlgorithmCrypt {
    @Override
    public String alg(String mode, String line, int count) {
        mode = "";
        for (int i = 0; i < line.length(); i++) {
            char ch = (char)(line.charAt(i));
            ch -= count;
            mode += ch;
        }
        return mode;
    }
}

class ShiftAlgorithmCrypt implements AlgorithmCrypt {
    @Override
    public String alg(String mode, String line, int count) {
        AlgorithmCrypt crt = null;
        if (mode == "enc") {
            crt = new EncShiftAlgorithmCrypt();
        } else  {
            crt = new DecShiftAlgorithmCrypt();
        }

        return crt.alg(mode, line, count);
    }
}

class EncShiftAlgorithmCrypt extends UnicodeAlgorithmCrypt {
    @Override
    public String alg(String mode, String line, int count) {
        mode = "";
        for (int i = 0; i < line.length(); i++) {
            char ch = (char)(line.charAt(i));
            int indexCh = (int)ch;
            if (indexCh >= 97 && indexCh <= 122) {
                if (indexCh + count > 122) {
                    int next = (ch + count) % 122;
                    ch = (char)(96 + next);
                } else {
                    ch = (char)(indexCh + count);
                }
            }

            if (indexCh >= 65 && indexCh <= 90) {
                if (indexCh + count > 90) {
                    int next = (ch + count) % 90;
                    ch = (char)(64 + next);
                } else {
                    ch = (char)(indexCh + count);
                }
            }


            mode += ch;
        }
        return mode;
    }
}

class DecShiftAlgorithmCrypt extends UnicodeAlgorithmCrypt {
    @Override
    public String alg(String mode, String line, int count) {
        mode = "";
        for (int i = 0; i < line.length(); i++) {
            char ch = (char)(line.charAt(i));
            int indexCh = (int)ch;
            if (indexCh >= 97 && indexCh <= 122) {
                if (indexCh - count >= 97) {
                    ch = (char)(indexCh - count);
                } else {
                    int next = 97 - (ch - count);
                    ch = (char)(123 - next);
                }
                mode += ch;
            }

            if (indexCh >= 65 && indexCh <= 90) {
                if (indexCh - count >= 65) {
                    ch = (char)(indexCh - count);
                } else {
                    int next = 65 - (ch - count);
                    ch = (char)(91 - next);
                }
                mode += ch;
            }

        }
        return mode;
    }
}

public class Main {
    public static void main(String[] args) {
        Cryption crypt = new Cryption(args);
        crypt.cryption();

    }
}
