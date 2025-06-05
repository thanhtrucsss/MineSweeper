package src;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class ScoreFileHandler {

    public static void saveScoreToFile(String fileName, long time, int score) {
        try (FileWriter fileWriter = new FileWriter(fileName, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write("TIME:" + time + "|SCORE:" + score);
            bufferedWriter.newLine();
            System.out.println("Score saved to file " + fileName + ": time=" + time + "s, score=" + score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveScore(long time, int score) {
        int levelNumber = Level.getLevelInNumber();
        String fileName;
        switch (levelNumber) {
            case 1:
                fileName = "src/txt/EasyLevelTimeRecords.txt";
                break;
            case 2:
                fileName = "src/txt/MediumLevelTimeRecords.txt";
                break;
            case 3:
                fileName = "src/txt/HardLevelTimeRecords.txt";
                break;
            default:
                fileName = "CustomLevelTimeRecords.txt";
        }
        saveScoreToFile(fileName, time, score);
        saveScoreToFile("src/txt/AllLevelRecords.txt", time, score);
    }

    // Đọc list [time, score] từ file
    public static List<long[]> readScoreFromFile(String fileName) {
        List<long[]> list = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("|")) {
                    String[] parts = line.split("\\|");
                    long time = Long.parseLong(parts[0].replace("TIME:", "").trim());
                    int score = Integer.parseInt(parts[1].replace("SCORE:", "").trim());
                    list.add(new long[]{time, score});
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Có thể in log hoặc bỏ qua nếu file chưa tồn tại
        }
        return list;
    }

    // Đọc tất cả thành tích dưới dạng chuỗi gốc từ file tổng hợp
    public static List<String> readAllScores(String fileName) {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) records.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    // Xếp hạng, show top 10 cho từng chế độ
    public static String toStringScore(String fileName) {
        List<long[]> list = readScoreFromFile(fileName);
        // Sắp xếp theo score giảm dần, cùng điểm thì thời gian tăng dần
        list.sort((a, b) -> {
            if (a[1] != b[1]) return Long.compare(b[1], a[1]); // Score giảm dần
            return Long.compare(a[0], b[0]); // Thời gian tăng dần
        });
        StringBuilder sb = new StringBuilder();
        sb.append("Top 10 Completion Rankings for this level:\n");
        sb.append(String.format("%-5s%-10s%-10s\n", "Rank", "src.Time(s)", "Score"));
        int rank = 1;
        for (long[] entry : list) {
            if (rank > 10) break;
            sb.append(String.format("%-5d%-10d%-10d\n", rank, entry[0], entry[1]));
            rank++;
        }
        // Nếu chưa đủ 10 entry thì hiển thị Null
        for (; rank <= 10; rank++)
            sb.append(String.format("%-5d%-10s%-10s\n", rank, "Null", "Null"));
        return sb.toString();
    }

    // Xếp hạng tổng hợp ALL MODES (top 10 điểm cao nhất)
    public static String allModesRanking() {
        List<long[]> scores = new ArrayList<>();
        List<String> records = readAllScores("src/txt/AllLevelRecords.txt");
        for (String line : records) {
            if (line.contains("|")) {
                String[] parts = line.split("\\|");
                long time = Long.parseLong(parts[0].replace("TIME:", "").trim());
                int score = Integer.parseInt(parts[1].replace("SCORE:", "").trim());
                scores.add(new long[]{time, score});
            }
        }
        // Sắp xếp theo score giảm dần, cùng điểm thì thời gian tăng dần
        scores.sort((a, b) -> {
            if (a[1] != b[1]) return Long.compare(b[1], a[1]);
            return Long.compare(a[0], b[0]);
        });

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 ALL MODES RANKING (Top 10)\n");
        sb.append(String.format("%-5s%-10s%-10s\n", "Rank", "src.Time(s)", "Score"));
        int rank = 1;
        for (long[] entry : scores) {
            if (rank > 10) break;
            sb.append(String.format("%-5d%-10d%-10d\n", rank, entry[0], entry[1]));
            rank++;
        }
        for (; rank <= 10; rank++)
            sb.append(String.format("%-5d%-10s%-10s\n", rank, "Null", "Null"));
        return sb.toString();
    }
}
