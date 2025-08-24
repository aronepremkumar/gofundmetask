package gofundme;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoFundMeProcessor {
    //Donors and their balance limits
    private static Map<String, Double> donorLimit = new HashMap<String, Double>();
    //Campaigns and their total balances
    private static Map<String, Double> campaignBalanceMap = new HashMap<String, Double>();
    //Donors and their campaign amounts donated
    private static Map<String, List<DonorCampaign>> donorCampaignMap = new HashMap<String, List<DonorCampaign>>();
    //Maps to store original names with cases
    private static Map<String, String> donorNameMap = new HashMap<String, String>();
    //Maps to store original campaign names with cases
    private static Map<String, String> campaignNameMap = new HashMap<String, String>();
    // Utility class instance
    private static Utility utility = new Utility();

    /**
     * Get all donor names
     *
     * @return
     */
    public static Set<String> getDonorNames() {
        return donorLimit.keySet();
    }

    /*
     * Get all campaign names
     * @return
     */
    public static Set<String> getCampaignNames() {
        return campaignBalanceMap.keySet();
    }

    /**
     * Compute average donation amount per donor
     *
     * @param donorName
     * @return
     */
    public static Map<String, Double> computeTotalAndAverage(String donorName) {
        Map result = new HashMap<String, Double>();
        double total = 0.0;
        double average = 0.0;
        List<DonorCampaign> donorCampaigns = donorCampaignMap.get(donorName.toLowerCase());
        try{
            if(donorCampaigns!=null){
                int size = (donorCampaigns!=null) ? donorCampaigns.size(): 0;
                Iterator<DonorCampaign> iterator = donorCampaigns.iterator();
                while (iterator.hasNext()) {
                    DonorCampaign donorCampaign = iterator.next();
                    average += donorCampaign.getCampaignAmount();
                }
                total = average;
                average = (size > 0) ? average / size : 0.0;
            }
            result.put("total", total);
            result.put("average", average);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Print summary of donors and campaigns
     */
    public static void printSummary() {
        System.out.println("Printing Summary...");
        System.out.println("=================================");
        System.out.println("Donors:");
        // Print donor balances
        //order by donor name
        //compute total and average donation per donor
        TreeSet<String> donorSet = new TreeSet<String>(donorLimit.keySet());
        for (String donor : donorSet) {
            System.out.println(donorNameMap.get(donor.toLowerCase()) + ": Total: $" + computeTotalAndAverage(donor).get("total") + " Average: " + computeTotalAndAverage(donor).get("average"));
        }
        System.out.println(" ");
        // Print campaign names and their total balances
        System.out.println("Campaigns:");
        //order by campaign name
        TreeSet<String> campaignSet = new TreeSet<String>(campaignBalanceMap.keySet());
        for (String campaign : campaignSet) {
            System.out.println(campaignNameMap.get(campaign.toLowerCase()) + ": Total: $" + campaignBalanceMap.get(campaign));
        }
        System.out.println(" ");
        System.out.println("Response code: 200 OK");
        System.out.println("=================================");
    }


    /**
     * Process each line of input
     *
     * @param eachLine
     */
    public static void processEachLine(String eachLine) {
        System.out.println("Processing line: " + eachLine);
        String[] parts = eachLine.split(" ");
        int len = parts.length;
        String donorName = "";
        String campaignName = "";
        String amountStr = "";
        String typeOfCommand = "";

        //identify the command type and the case
        Set<String> donors = getDonorNames();
        Set<String> campaigns = getCampaignNames();
        Matcher matcher;

        Pattern ADD_DONOR = Pattern.compile(
                "^\\s*add\\s+donor\\s+(.+?)\\s+\\$?(\\d+(?:\\.\\d{1,2})?)\\s*$",
                Pattern.CASE_INSENSITIVE
        );
        Pattern ADD_CAMPAIGN = Pattern.compile(
                // "^Add Campaign (.+)$",
                "^(?i)add\\s+campaign\\s+([A-Za-z0-9 ]+)$",
                Pattern.CASE_INSENSITIVE
        );

        if ((matcher = ADD_DONOR.matcher(eachLine)).matches()) {
            donorName = matcher.group(1).trim();
            //double limit = Double.parseDouble(matcher.group(2));
            amountStr = parts[len - 1];
            //System.out.println("Donor: " + donorName + " Limit: " + amountStr);
            typeOfCommand = "addDonor";
        } else if ((matcher = ADD_CAMPAIGN.matcher(eachLine)).matches()) {
            campaignName = matcher.group(1).trim();
            //System.out.println("Campaign: " + campaignName);
            typeOfCommand = "addCampaign";
        } else if (parts[0].equalsIgnoreCase("donate")) {
            amountStr = parts[len - 1];
            donorName = donors.stream()
                    .filter(k -> eachLine.toLowerCase().contains(k.toLowerCase()))
                    .findFirst()
                    .orElse("");

            campaignName = campaigns.stream()
                    .filter(k -> eachLine.toLowerCase().contains(k.toLowerCase()))
                    .findFirst()
                    .orElse("");

            //System.out.println("Donor: " + donorName + ", Campaign: " + campaignName + ", Amount: " + amountStr);
            typeOfCommand = "donate";
        } else {
            System.out.println("Invalid command: " + eachLine);
        }


        if (typeOfCommand.equalsIgnoreCase("addDonor")) {
            //Add donor case
            //if not empty string remove the dollar sign in the front
            if (!(amountStr == null || amountStr.isEmpty()) && amountStr.startsWith("$"))
                amountStr = amountStr.substring(1);
            double balance = utility.isStringNumber(amountStr) ? Double.parseDouble(amountStr) : 0;
            //validate donor name and balance > 0
            if (utility.isValidString(donorName) && balance > 0) {
                System.out.println("Adding donor: " + donorName + " with balance: " + balance);
                //add to donor limits
                if (donorLimit.containsKey(donorName.toLowerCase())) {
                    //if donor already exists, update the limit by adding to existing balance
                    double existingBalance = donorLimit.get(donorName.toLowerCase());
                    balance = existingBalance + balance;
                } else {
                    //store original case name
                    donorNameMap.put(donorName.toLowerCase(), donorName);
                }
                donorLimit.put(donorName.toLowerCase(), balance);

            } else {
                System.out.println("Donor name or balance not found.");
            }
        } else if (typeOfCommand.equalsIgnoreCase("addCampaign")) {
            //Add campaign case
            if (utility.isValidString(campaignName)) {
                System.out.println("Adding campaign: " + campaignName);
                //add to campaign balance map
                if (!campaignBalanceMap.containsKey(campaignName.toLowerCase())) {
                    campaignBalanceMap.put(campaignName.toLowerCase(), 0.0);
                    campaignNameMap.put(campaignName.toLowerCase(), campaignName);
                }

            } else {
                System.out.println("Invalid campaign name.");
            }

        } else if (typeOfCommand.equalsIgnoreCase("donate")) {
            if (!(amountStr == null || amountStr.isEmpty()) && amountStr.startsWith("$"))
                amountStr = amountStr.substring(1);
            double amount = utility.isStringNumber(amountStr) ? Double.parseDouble(amountStr) : 0;
            //check if donor and campaign exist
            boolean isValidDonor = (!donorLimit.containsKey(donorName.toLowerCase())) ? false : true;
            boolean isValidCampaign = (!campaignBalanceMap.containsKey(campaignName.toLowerCase())) ? false : true;
            boolean insufficientBalance = false;
            //check if donor has enough balance
            if (isValidDonor) {
                double existingBalance = donorLimit.get(donorName.toLowerCase());
                insufficientBalance = (existingBalance < amount) ? true : false;

            }
            //process donation if valid donor, campaign and sufficient balance
            if (isValidDonor && isValidCampaign && !insufficientBalance && amount > 0) {
                System.out.println("Processing donation of $" + amount + " from donor " + donorName + " to campaign " + campaignName);
                //deduct balance from donor
                double existingBalance = donorLimit.get(donorName.toLowerCase());
                double newBalance = existingBalance - amount;
                donorLimit.put(donorName.toLowerCase(), newBalance);
                //add balance to campaign
                double existingCampaignBalance = campaignBalanceMap.get(campaignName.toLowerCase());
                double newCampaignBalance = existingCampaignBalance + amount;
                campaignBalanceMap.put(campaignName.toLowerCase(), newCampaignBalance);

                //add entry to donor-campaign map to track donations
                DonorCampaign donorCampaign = new DonorCampaign();
                donorCampaign.setDonorName(donorName.toLowerCase());
                donorCampaign.setCampaignName(campaignName.toLowerCase());
                donorCampaign.setCampaignAmount(amount);
                if (donorCampaignMap.containsKey(donorName.toLowerCase())) {
                    List<DonorCampaign> existingList = donorCampaignMap.get(donorName.toLowerCase());
                    existingList.add(donorCampaign);
                    donorCampaignMap.put(donorName.toLowerCase(), existingList);
                } else {
                    List<DonorCampaign> newList = new ArrayList<DonorCampaign>();
                    newList.add(donorCampaign);
                    donorCampaignMap.put(donorName.toLowerCase(), newList);
                }

            } else {
                System.out.println("Invalid donor or campaign or insufficient balance.");
            }
        } else {
            System.out.println("Invalid command or format.");
        }
        System.out.println("============================================================================ ");
    }

    public static void main(String[] args) {
        System.out.println("Processing GoFundMe campaigns...");
        System.out.println("=================================");
        BufferedReader bufferedReader;
        // Case 1: File provided as argument
        if (args.length > 0) {
            System.out.println("Reading from file: " + args[0]);
            System.out.println("=================================");
            try {
                bufferedReader = new BufferedReader(new FileReader(args[0]));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        // Case 2: Read from stdin (for piped input)
        else {
            System.out.println("Reading from standard input...");
            System.out.println("=================================");
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    if (line.equalsIgnoreCase("END")) break;
                    processEachLine(line);
                } catch (Exception e) {
                    System.err.println("Error processing line: " + line);
                }
            }
            printSummary();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
