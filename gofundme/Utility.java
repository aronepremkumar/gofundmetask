package gofundme;

public class Utility {

    /**
     * Validate if the string is null or empty
     * @param name
     * @return
     */
    public boolean isValidString(String name) {
        //validate donor name
        boolean isValid = true;
        isValid = (name == null || name.isEmpty()) ? false : true;
        //System.out.println("Name " + name + " is valid: " + isValid);
        return isValid;
    }

    /**
     * Validate if the string is a number
     * @param str
     * @return
     */
    public boolean isStringNumber(String str) {
        try {
            Double.parseDouble(str);
            //System.out.println("String " + str + " is a valid number.");
            return true;
        } catch (NumberFormatException e) {
            //System.out.println("String " + str + " is not a valid number.");
            return false;
        }
    }
}
