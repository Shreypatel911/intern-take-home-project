package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // this infinite while is for testing with other file or API endpoint after one is done
        // do not have to run the program again
        while(true){
            Scanner scanner = new Scanner(System.in);
            List<IndexData> data = null;
            int choice;

            // two options are available for input
            // either from input JSON file or from response of API endpoint
            // till the program don't get a valid input it will keep on asking for it that is why there is infinite loop
            while(true){
                System.out.println("Select input mode:");
                System.out.println("1. From JSON file");
                System.out.println("2. From API endpoint");

                String input = scanner.nextLine().trim();

                if(input.equals("1") || input.equals("2")){
                    choice = Integer.parseInt(input);
                    break;
                }else{
                    System.out.println("Invalid input. Please enter 1 or 2.\n");
                }
            }

            // try catch to track if there is error while reading data
            try{
                if(choice == 1){
                    // if user wants to give input from input JSON file
                    // whichever file you want to use it should be in resources folder
                    while(true){
                        System.out.print("Enter the name of input JSON file: ");
                        String filename = scanner.nextLine();
                        filename += ".json";
                        // to get from resource folder inside src folder
                        InputStream inputFile = Main.class.getClassLoader().getResourceAsStream(filename);
                        if(inputFile == null){
                            // if there is no file with give name
                            System.out.println("File not found in resources. Please enter a valid file name.\n");
                            continue;
                        }
                        try{
                            String content = new String(inputFile.readAllBytes());
                            JSONArray arr = new JSONArray(content);
                            data = parseJsonArray(arr);
                            break;
                        }catch(Exception e){
                            System.out.println("Something went wrong while reading the input JSON file\n");
                        }
                    }

                }else{
                    System.out.print("Enter API endpoint ");
                    String endpoint = scanner.nextLine().trim();

                    int year, month, day;
                    // checking for valid year, month and day till I do not found one correct date
                    while(true){
                        System.out.print("Enter year (e.g., 2025): ");
                        String y = scanner.nextLine().trim();
                        try{
                            year = Integer.parseInt(y);
                            if(year >= 1950 && year <= 2100)
                                break;
                            else
                                System.out.println("Year should be between 1950 and 2100\n");
                        }catch(NumberFormatException e){
                            System.out.println("Only number is allowed for input Year\n");
                        }
                    }

                    while(true){
                        System.out.print("Enter month (1–12): ");
                        String m = scanner.nextLine().trim();
                        try{
                            month = Integer.parseInt(m);
                            if(month >= 1 && month <= 12)
                                break;
                            else
                                System.out.println("Month should be between 1 and 12\n");
                        }catch(NumberFormatException e){
                            System.out.println("Only number is allowed for input Month\n");
                        }
                    }

                    while(true){
                        System.out.print("Enter day (1–31): ");
                        String d = scanner.nextLine().trim();
                        try{
                            day = Integer.parseInt(d);
                            if(day >= 1 && day <= 31)
                                break;
                            else
                                System.out.println("Day should be between 1 and 31\n");
                        }catch(NumberFormatException e){
                            System.out.println("Only number is allowed for input Day\n");
                        }
                    }

                    // making a URL from the given information from the user
                    String url = String.format(
                            "https://%s/_cat/indices/*%d*%02d*%02d?v&h=index,pri.store.size,pri&format=json&bytes=b",
                            endpoint, year, month, day
                    );
                    System.out.println(url);

                    // making a API call to given endpoint
                    try{
                        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept", "application/json");

                        if(conn.getResponseCode() != 200){
                            System.out.println("API request failed with status code: " + conn.getResponseCode());
                        }

                        InputStream in = conn.getInputStream();
                        String json = new String(in.readAllBytes());
                        JSONArray arr = new JSONArray(json);
                        data = parseJsonArray(arr);
                        conn.disconnect();
                    }catch(Exception e){
                        System.out.println("Something went wrong while fetching data from the API.");
                    }
                }
            }catch(Exception e){
                System.err.println("Error reading data in either of the options: " + e.getMessage());
            }

            // it is possible that size of index can be 0 and in that case number of shards would also be 0
            // for that I am just ignoring those indexes to avoid calculation like 0/0
            List<IndexData> filteredData = new ArrayList<>();
            for(IndexData d : data){
                if(!(d.sizeInBytes.equals(BigInteger.ZERO) && d.numberOfShards == 0)){
                    filteredData.add(d);
                }
            }

            printTopIndexesBySize(filteredData);
            printTopIndexesByShards(filteredData);
            printLeastBalancedIndexes(filteredData);
            System.out.println();
        }
    }

    private static List<IndexData> parseJsonArray(JSONArray arr){
        // parsing entire JSON array and converting it to list of Objects (where objects will be of class IndexData)
        List<IndexData> result = new ArrayList<>();
        for(int i=0;i<arr.length();i++){
            JSONObject obj = arr.getJSONObject(i);
            String name = obj.getString("index");
            BigInteger size = new BigInteger(obj.getString("pri.store.size"));
            int shards = Integer.parseInt(obj.getString("pri"));
            result.add(new IndexData(name, size, shards));
        }
        return result;
    }

    private static void printTopIndexesBySize(List<IndexData> data){
        System.out.println("\nPrinting largest indexes by storage size");

        // sorting in decreasing order by size in GB
        Collections.sort(data, (a, b) -> Double.compare(b.getSizeInGB(), a.getSizeInGB()));

        // it is possible that the data size if less than 5
        // if it is the case then I am only printing the available data
        for(int i=0;i<Math.min(5, data.size());i++){
            IndexData d = data.get(i);
            System.out.println("Index: " + d.index);
            System.out.printf("Size: %.2f GB\n", d.getSizeInGB());
        }
    }


    private static void printTopIndexesByShards(List<IndexData> data){
        System.out.println("\nPrinting largest indexes by shard count");

        // sorting in decreasing order by number of shards
        Collections.sort(data, (a, b) -> Integer.compare(b.numberOfShards, a.numberOfShards));

        for(int i=0;i<Math.min(5, data.size());i++){
            IndexData d = data.get(i);
            System.out.println("Index: " + d.index);
            System.out.println("Shards: " + d.numberOfShards);
        }
    }


    private static void printLeastBalancedIndexes(List<IndexData> data){
        System.out.println("\nPrinting least balanced indexes");

        // sorting in decreasing order of ratio of size to number of shards allocated
        Collections.sort(data, (a, b) -> Double.compare(b.getBalanceRatio(), a.getBalanceRatio()));

        for(int i=0;i<Math.min(5, data.size());i++){
            IndexData d = data.get(i);
            System.out.println("Index: " + d.index);
            System.out.printf("Size: %.2f GB\n", d.getSizeInGB());
            System.out.println("Shards: " + d.numberOfShards);
            double exact = d.getBalanceRatio();
            long floor = (long)Math.floor(exact);
            long rounded = (exact - floor) >= 0.5 ? floor + 1 : floor;
            System.out.println("Balance Ratio: " + rounded);
            System.out.println("Recommended shard count is " + d.getRecommendedShardCount());
        }
    }

}