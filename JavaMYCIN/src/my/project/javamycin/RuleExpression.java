package my.project.javamycin;

import java.util.ArrayList;

/**
 * Object that represents an instance of a given rule
 * Rules are made up of judgement operators
 */
public class RuleExpression {
    public ArrayList<JudgementOperator> premises;
    public Conclusion conclusion;
    public boolean isUsed;

    RuleExpression() {
        this.premises = new ArrayList<JudgementOperator>();
        this.conclusion = new Conclusion();
        this.isUsed = false;

    } // RuleExpression

    /**
     * ANDs every judgement expression together and returns the truth value of the expression if evaluable
     * If not, null indicates a backchain needs to happen
     * @return The truth value of the rule expression or null if an expression cannot be evaluated due to an unknown parameter
     */
    public Boolean evaluate(Context c) {
        Boolean isRuleSatisfied = true;

        // Evalute all of the premises
        for (int i = 0; i < premises.size(); i++) {
            String parameterInQuestion = premises.get(i).parameterToJudge;
            String presumption = null;

            //See if there is a presumed value for the parameter or if it is currently null (meaning it hasn't been backchained and isn't given as unk (unknown))
            for (int j = 0; j < c.parameters.size(); j++) {
                if (c.parameters.get(j).parameterName.equals(parameterInQuestion)) {
                    presumption = c.parameters.get(j).presumedValue;
                    j = c.parameters.size();
                } // if
            } // for

            //Check if if the presumed value needs to be backchained for...
            if (premises.get(i).evaluate(presumption) == null) {
                isRuleSatisfied = null;
                // Store what parameter was just judged in Context so it can be used in JavaMYCIN to push the next backchain subgoal
                c.pushSubGoal = premises.get(i).parameterToJudge;
                i = premises.size();

            // Or presumed value makes the premise false (value doesn't match, unk).
            } else if (premises.get(i).evaluate(presumption).equals(Boolean.FALSE)) {
                isRuleSatisfied = false;
                i = premises.size();

            } // if

        } // for

        return isRuleSatisfied;
    } // evaluate

    /**
     * Judgement operators are logical premises that are autofilled
     * The judgement operators determine which question is being asked and about what attribute of
     * Snakes and the Input or Input against some constant.
     */
    public class JudgementOperator {
        public String parameterToJudge;
        public String valuePart;

        JudgementOperator() {
            this.parameterToJudge = "";
            this.valuePart = "";

        } // JudgementOperator

        JudgementOperator(String pj, String vp) {
            this.parameterToJudge = pj;
            this.valuePart = vp;

        } // JudgementOperator

        public Boolean evaluate(String presumedValue) {
            Boolean b = true;

            if (presumedValue == "") { 
                // Input not known, needs to be back-chained or prompted for
                //System.out.println(this.parameterToJudge + " isn't given");
                b = null;
            
            } else {
                b = presumedValue.equals(valuePart);

            } // if  
            
            return b;

        } // evaluate

        public void describeJudgementOperator() {
            System.out.print("IF " + parameterToJudge + " is " + valuePart);

        }

    } // JudgementOperator

    /**
     * Represents the action to take when a rule expression is satisfied
     * When a rule expression is satisfied, it tells JavaMYCIN that this event
     * it should indicates a certain parameter is more or less likely
     */
    public class Conclusion {
        public String parameterToConclude;
        public String parameterValue;
        public int incrementTally;
        public boolean isUsed;

        Conclusion() {
            this.parameterToConclude = "";
            this.parameterValue = "";
            this.incrementTally = 0;
            this.isUsed = false;
        } // Conclusion

        Conclusion(String pc, String pv, int it) {
            this.parameterToConclude = pc;
            this.parameterValue = pv;
            this.incrementTally = it;
            this.isUsed = false;
        } // Conclusion

        Conclusion(Conclusion c) {
            this.parameterToConclude = c.parameterToConclude;
            this.parameterValue = c.parameterValue;
            this.incrementTally = c.incrementTally;
            this.isUsed = false;
        } // Conclusion

    } // Conclusion

} // RuleExpression