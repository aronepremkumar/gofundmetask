package gofundme;

import java.io.*;
import java.util.*;

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
     * Compute average donation amount per donor
     * @param donorName
     * @return
     */
    public static double computeAverage(String donorName) {
        double average = 0.0;
        List<DonorCampaign> donorCampaigns = donorCampaignMap.get(donorName.toLowerCase());
        int size = donorCampaigns.size();
        Iterator<DonorCampaign> iterator = donorCampaigns.iterator();
        while (iterator.hasNext()) {
            DonorCampaign donorCampaign = iterator.next();
            average += donorCampaign.getCampaignAmount();
        }
        return average / size;
    }

    /**
     * Print summary of donors and campaigns
     */
    public static void printSummary() {
        System.out.println("Printing Summary...");
        System.out.println("Donors:");
        // Print donor balances
        //order by donor name
        //compute total and average donation per donor
        TreeSet<String> donorSet = new TreeSet<String>(donorLimit.keySet());
        for (String donor : donorSet) {
            System.out.println(donorNameMap.get(donor.toLowerCase()) + ": Total: $" + donorLimit.get(donor) + " Average: " + computeAverage(donor));
        }
        System.out.println(" ");
        // Print campaign names and their total balances
        System.out.println("Campaigns:");
        //order by campaign name
        TreeSet<String> campaignSet = new TreeSet<String>(campaignBalanceMap.keySet());
        for (String campaign : campaignSet) {
            System.out.println(campaignNameMap.get(campaign.toLowerCase()) + ": Total: $" + campaignBalanceMap.get(campaign));
        }
        System.out.println("Response code: 200");
    }


    /**
     * Process each line of input
     * @param eachLine
     */
    public static void processEachLine(String eachLine) {
        System.out.println("Processing line: " + eachLine);
        String[] parts = eachLine.split(" ");
        String command = parts[0];
        String type = parts[1];
        int length = parts.length;

        if (command.equalsIgnoreCase("add") && type.equalsIgnoreCase("donor")) {
            //Add donor case
            if (length == 4) {
                //validate donor name and balance
                String donorName = parts[2];
                String balanceStr = parts[3];
                //if not empty string remove the dollar sign in the front
                if (!(balanceStr == null || balanceStr.isEmpty()) && balanceStr.startsWith("$"))
                    balanceStr = balanceStr.substring(1);
                double balance = utility.isStringNumber(balanceStr) ? Double.parseDouble(balanceStr) : 0;
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
                    System.out.println("Invalid donor name or balance.");
                }
            } else {
                System.out.println("Donor name or balance not found.");
            }
        } else if (command.equalsIgnoreCase("add") && type.equalsIgnoreCase("campaign")) {
            //Add campaign case
            if (length == 3) {
                String campaignName = parts[2];
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
            } else {
                System.out.println("Invalid add campaign format.");
            }
        } else if (command.equalsIgnoreCase("donate") && length == 4) {
            String donorName = parts[1];
            String campaignName = parts[2];
            String amountStr = parts[3];
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
    }

    public static void main(String[] args) {
        System.out.println("Processing GoFundMe campaigns...");
        BufferedReader bufferedReader;
        // Case 1: File provided as argument
        if (args.length > 0) {
            System.out.println("Reading from file: " + args[0]);
            try {
                bufferedReader = new BufferedReader(new FileReader(args[0]));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        // Case 2: Read from stdin (for piped input)
        else {
            System.out.println("Reading from standard input...");
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
