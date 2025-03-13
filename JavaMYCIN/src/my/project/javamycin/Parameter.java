package my.project.javamycin;

import java.util.ArrayList;

/** 
 *  
*/
public class Parameter {
    public String parameterName;
    public String prompt;
    public boolean isAGoal;
    public PotentialValue[] potentialValues;
    public String presumedValue;

    public Parameter() {
        this.parameterName = "";
        this.prompt = "";
        this.isAGoal = false;
        this.potentialValues = new PotentialValue[0];
        this.presumedValue = "";

    } // Parameter

    public Parameter(String n, String p, boolean g, ArrayList<String> pv) {
        this.parameterName = n;
        this.prompt = p;
        this.isAGoal = g;

        this.potentialValues = new PotentialValue[pv.size()];

        for (int i  = 0; i < pv.size(); i++) {
            PotentialValue tempPv = new PotentialValue();
            tempPv.valueName = pv.get(i);
            tempPv.tally = 0;

            this.potentialValues[i] = tempPv;

        } // for

        this.presumedValue = "";

    } // Parameter

    public Parameter(String s1, String s2) {
        this.parameterName = s1;
        this.presumedValue = s2;
    } // Parameter

    /** 
     *  
    */
    public class PotentialValue {
        public String valueName;
        public int tally;

        PotentialValue() {
            this.valueName = "";
            this.tally = 0;

        } // ParameterValue

    } // PotentialValue

} // Parameter