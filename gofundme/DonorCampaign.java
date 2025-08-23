package gofundme;
public class DonorCampaign {
    String donorName;
    String campaignName;
    double CampaignAmount;

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public double getCampaignAmount() {
        return CampaignAmount;
    }

    public void setCampaignAmount(double campaignAmount) {
        CampaignAmount = campaignAmount;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
}