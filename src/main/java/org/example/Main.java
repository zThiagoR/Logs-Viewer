package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

public class Main {

    public static void main(String[] args) {
        String targetFolder = "CraftLandia Minecraft";
        String initialPath = getDesktopPath();
        String desiredFile = "output-client.log";

        File craftlandiaPath = searchFolder(targetFolder, initialPath);
        if (craftlandiaPath != null) {
            String filePath = craftlandiaPath.getAbsolutePath() + File.separator + "CraftLandia" + File.separator
                    + "1.5" + File.separator + "data" + File.separator + ".minecraft" + File.separator + desiredFile;
            File file = new File(filePath);

            if (file.exists()) {
                System.out.println("Arquivo encontrado: " + file.getAbsolutePath());

                try {
                    String nick = promptNick();
                    String currentPath = System.getProperty("user.dir");
                    String logsFolderPath = currentPath + File.separator + "logs";
                    String filteredMessages = filterMessagesByNick(file, nick);
                    String filteredFileName = "logs-" + nick + ".txt";

                    File logsFolder = new File(logsFolderPath);
                    if (!logsFolder.exists()) {
                        boolean created = logsFolder.mkdirs();
                        if(!created) {
                            System.out.println("Criação da pasta 'logs' falhado");
                        }
                    }

                    String filteredFilePath = logsFolderPath + File.separator + filteredFileName;

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(filteredFilePath)), StandardCharsets.UTF_8));
                    writer.write(filteredMessages);
                    writer.close();

                    System.out.println("\n\n\nArquivo filtrado criado com sucesso: " + filteredFileName);
                } catch (IOException e) {
                    System.err.println("Erro ao ler o arquivo: " + e.getMessage());
                }
            } else {
                System.out.println("Arquivo não encontrado: " + desiredFile);
            }
        } else {
            System.out.println("Pasta 'CraftLandia Minecraft' não encontrada na área de trabalho.");
        }
    }

    private static String getDesktopPath() {
        FileSystemView filesys = FileSystemView.getFileSystemView();
        return filesys.getHomeDirectory().getAbsolutePath();
    }

    private static File searchFolder(String targetFolder, String initialPath) {
        File[] files = new File(initialPath).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().equals(targetFolder)) {
                    // Pasta "CraftLandia Minecraft" encontrada!
                    System.out.println("Pasta encontrada: " + file.getAbsolutePath());
                    return file;
                }
            }
        }

        return null;
    }

    private static String filterMessagesByNick(File file, String nick) throws IOException {
        StringBuilder filteredMessages = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(nick)) {
                    System.out.println(line);
                    String filteredLine = removeSpecialCharacters(line);
                    filteredMessages.append(filteredLine).append(System.lineSeparator());
                }
            }
        }

        if (filteredMessages.length() == 0) {
            System.err.println("Nenhuma mensagem encontrada para o nick \"" + nick + "\".");
            System.exit(1);
        }

        return filteredMessages.toString();
    }

    private static String removeSpecialCharacters(String text) {
        return text.replaceAll("�[0-9a-fk-or]", "");
    }

    private static String promptNick() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Qual nick deseja filtrar as mensagens? ");
        String nick = scanner.nextLine();
        scanner.close();

        if (nick.isEmpty()) {
            throw new IllegalArgumentException("O nick não pode estar vazio.");
        }

        return nick;
    }
}