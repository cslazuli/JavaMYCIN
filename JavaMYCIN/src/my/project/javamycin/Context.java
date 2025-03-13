package my.project.javamycin;

import java.util.ArrayList;

/**
 * Represents a context which a consultation can be performed on.
 */
public class Context {
    public String name;
    public ArrayList<Parameter> parameters;
    public ArrayList<RuleExpression> rules;
    public String pushSubGoal;

    public void describeContext() {
        System.out.println("Context Name: " + name);

        for (int i = 0; i < parameters.size(); i++) {
            System.out.print(" Parameter");

            if (parameters.get(i).isAGoal) {
                System.out.print(" (Goal)");
            } // if

            System.out.println(": " + parameters.get(i).parameterName);

            System.out.println(" Prompt: " + parameters.get(i).prompt);
            System.out.println(" Expected Values:");
                        
            for (int j = 0; j < parameters.get(i).potentialValues.length; j++) {
                System.out.println("  " + parameters.get(i).potentialValues[j].valueName);
            }

            if (!parameters.get(i).presumedValue.equals("")) {
                System.out.println(" Presumed Value: " + parameters.get(i).presumedValue);
            } // if

            System.out.println();

        } // for

    } // describeContext

    public void describeRules() {
        int ruleNum = 1;

        for (int i = 0; i < rules.size(); ruleNum++, i++) {
            System.out.println("Rule #" + ruleNum);
                        
            for (int j = 0; j < rules.get(i).premises.size(); j++) {
                rules.get(i).premises.get(j).describeJudgementOperator();

                if (j < rules.get(i).premises.size()-1) {
                    System.out.println(" &&");
                }

            } // for

            System.out.println("\nTHEN conclude that " + rules.get(i).conclusion.parameterToConclude + " is " + rules.get(i).conclusion.parameterValue + ". Add " + rules.get(i).conclusion.incrementTally + " To its tally.");
            System.out.println();

        } // for

    } // describeRules

    public void describeRuleAt(int index) {
        int ruleNum = 1;

        for (int i = 0; i < rules.size(); i++, ruleNum++) {
            if (ruleNum == index+1) {

                System.out.println("Rule:");
                        
                for (int j = 0; j < rules.get(i).premises.size(); j++) {
                    rules.get(i).premises.get(j).describeJudgementOperator();
    
                    if (j < rules.get(i).premises.size()-1) {
                        System.out.println(" &&");
                    }
    
                } // for

                System.out.println("\nTHEN conclude that " + rules.get(i).conclusion.parameterToConclude + " is " + rules.get(i).conclusion.parameterValue + ". Add " + rules.get(i).conclusion.incrementTally + " To its tally.");
                System.out.println();

            } // if

        } // for

    } // describeRules

    public void describeGoals() {
        System.out.println("\n-------------");

        System.out.println("Context Name: " + name);

        System.out.println(" Goal Outcomes: ");

        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i).isAGoal) {
                System.out.println("  " + parameters.get(i).parameterName + ": " + parameters.get(i).presumedValue);
            } // if
        } // for

        System.out.println("-------------\n");

    } // describeGoals

    public Context() {
        this.name = new String();
        this.parameters = new ArrayList<Parameter>();
        this.rules = new ArrayList<RuleExpression>();
        this.pushSubGoal = new String();

    } // Context

        /** 
     *  
    */
    public class Goal {
        public String goalParameterName;
        public int currentRuleIndex;
        public boolean hasConclusion;

        Goal() {
            this.goalParameterName = "";
            this.currentRuleIndex = 0;
            this.hasConclusion = false;

        } // Goal

        Goal(String s) {
            this.goalParameterName = s;
            this.currentRuleIndex = 0;
            this.hasConclusion = false;

        } // Goal

    } // Goal

} // Context

