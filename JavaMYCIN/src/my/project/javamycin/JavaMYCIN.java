package my.project.javamycin;

import java.io.File;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;

/** 
 * Virtual Machine for performing a expert system consultation.
 * Based on TMYCIN (“Tiny EMYCIN”) by Gordon S. Novak Jr. at UTexas Austin.
*/
public class JavaMYCIN {

/**
 * @param args args[1] is the [Full Context].txt, args[2] is the [Initial Inputs].txt. See provided files in the full_context and input folders for formatting.
 */
    public static void main(String[] args) {

        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java JavaMYCIN [complete context file] [user input file (optional)]");
            System.exit(1);
        }

        Context context = new Context();

        try {
            File contextFile = new File(args[0]);

            //Create context and rules first
            Scanner buffer = new Scanner(contextFile);

            String line = buffer.nextLine();
            //System.out.println(line);

            // mode 0 = defining context
            // mode 1 = defining rules
            int mode = 0;

            while (buffer.hasNext()) {
                if (line.equals("/DEFCONTEXT")) {
                    mode = 0;

                    line = buffer.nextLine();
                    line = buffer.nextLine();

                    context.name = line;
                    //System.out.println(context.name);

                    line = buffer.nextLine();
                    line = buffer.nextLine();

                } // if

                if (line.equals("/DEFRULES")) {
                    mode = 1;
                    line = buffer.nextLine();
                    line = buffer.nextLine();
                } // if

                if (mode == 0 && buffer.hasNextLine()) {
                    StringTokenizer st = new StringTokenizer(line);

                    String paramName = st.nextToken();
                    //System.out.println(paramName);

                    String goalString = st.nextToken();
                    boolean isGoal = false;
                    
                    if (goalString.equals("Goal")) {
                        isGoal = true;

                    } else if (goalString.equals("NonGoal")) {
                        isGoal = false;

                    } else {
                        System.out.println("isGoal expected.");
                        System.exit(1);

                    }

                    line = buffer.nextLine();

                    String prompt = line;

                    line = buffer.nextLine();

                    ArrayList<String> potentialValues = new ArrayList<String>();

                    st = new StringTokenizer(line);

                    while (st.hasMoreTokens()) {
                        potentialValues.add(st.nextToken());
                    }

                    Parameter p = new Parameter(paramName, prompt, isGoal, potentialValues);

                    context.parameters.add(p);

                    // Move tapehead to next parameter if not end of file
                    if (buffer.hasNextLine()) {
                        line = buffer.nextLine();
                        line = buffer.nextLine();
                    } // if

                } else if (mode == 1 && buffer.hasNextLine()) {
                    StringTokenizer st = new StringTokenizer(line);
                    RuleExpression re = new RuleExpression();

                    if (line.equals("/Rule")) {
                        line = buffer.nextLine();

                        while (!line.contains("Conclude ")) {
                            st = new StringTokenizer(line);
                            
                            String param = st.nextToken();
                            String valuePart = st.nextToken();

                            RuleExpression.JudgementOperator tempJo = re.new JudgementOperator(param, valuePart);
                            re.premises.add(tempJo);

                            line = buffer.nextLine();
                        } // while

                            //System.out.println("\nGetting the conclusions...\n");

                            st = new StringTokenizer(line);

                            // Move tapehead past "Conclude" token
                            String parameterToConclude = st.nextToken();

                            parameterToConclude = st.nextToken();
                            //System.out.println(parameterToConclude);

                            String valueToConclude = st.nextToken();
                            //System.out.println(valueToConclude);

                            int addTally = Integer.parseInt(st.nextToken());
                            //System.out.println(addTally);

                            RuleExpression.Conclusion tempConcl = re.new Conclusion(parameterToConclude, valueToConclude, addTally);
                            re.conclusion = re.new Conclusion(tempConcl);
                            
                        } // if

                        context.rules.add(re);

                        // Move tapehead to next rule if not end of file
                        if (buffer.hasNextLine()) {
                            line = buffer.nextLine();
                            line = buffer.nextLine();
                        } // if

                } // if

                    //buffer.close();
                    //return;

            } // while

            //context.describeContext();
            //context.describeRules();

            // Now, if you have them, store inputs as presumed values of parameters
            if (args.length == 2) {
                File inputFile = new File(args[1]);
                buffer = new Scanner(inputFile);

                //System.out.println("Inputs:");

                while (buffer.hasNext()) {
                    line = buffer.nextLine();
                    StringTokenizer st = new StringTokenizer(line);

                    // Format: [Parameter] [Value]

                    String param = st.nextToken();
                    //System.out.print(param + " ");
                    String inputValue = st.nextToken();
                    //System.out.println(inputValue);

                    
                    // Use param to match the parameter input to the parameter of the context
                    for (int i = 0; i < context.parameters.size(); i++) {
                        if (context.parameters.get(i).parameterName.equals(param)) {
                            // Then, change that parameter's presumedValue to input value
                            context.parameters.get(i).presumedValue = inputValue;

                        } // if
                    } // for

                    // This should work without changing the PotentialValue.tally because the tally only comes into play
                    // during backchains, which only happen if the presumedValue is unknown 

                } // while

            } // if

            buffer.close();

        } catch (FileNotFoundException fnfe) {
            System.err.println("FileNotFoundException: File not found.");
            System.exit(0);
        } // try

        //Enter the Shell

        Scanner keyboard = new Scanner(System.in);

        System.out.println(" ~ Welcome to the JavaMYCIN Shell ~\n");

        String keyInput = "";

        boolean stepByStepMode = true;
        boolean completedAConsultation = false;

        while (!keyInput.equals("Q")) {
            keyInput = "";

            System.out.println("Commands: ");
            if (completedAConsultation) {
                System.out.println("[W]hat rules were concluded to complete the consultation?");
                System.out.println("W[H]ich rules concluded true for a given parameter?");       
                System.out.println("[G]oal Readout");               
            } // if

            if (!completedAConsultation) {
                System.out.println("[B]egin new consultation using input file");

            } else {
                System.out.println("[B]egin a consultation using input file and the values deduced from the last one");

            } // else
            
            System.out.println("[K]nowledgebase Readout");
            System.out.println("[P]arameter List");
            System.out.println("[R]ule List");
            System.out.println("[A]nalyze Knowledgebase (Prints all rules that conclude a given parameter)");
            System.out.println("[D]escriptive Mode On? " + stepByStepMode);
            System.out.println("[Q]uit\n");

            System.out.print("$ ");
            keyInput = keyboard.next().toUpperCase();

            if (keyInput.equals("K")) {
                context.describeContext();
                context.describeRules();
            } // if

            if (keyInput.equals("R")) {
                context.describeRules();
            } // if

            if (keyInput.equals("P")) {
                context.describeContext();
            } // if

            if (keyInput.equals("D")) {
                stepByStepMode = !stepByStepMode;
            } // if

            if (keyInput.equals("A")) {
                System.out.println("Input a parameter to analyze.");
                System.out.print("Expected Values: ");

                for (int i = 0; i < context.parameters.size(); i++) {
                    System.out.print(context.parameters.get(i).parameterName + " ");
                } // for

                System.out.println();
                System.out.print("$ ");

                String parameterToAnalyze = keyboard.next();

                for (int i = 0; i < context.rules.size(); i++) {
                    if (context.rules.get(i).conclusion.parameterToConclude.equals(parameterToAnalyze)) {
                        context.describeRuleAt(i);
                    } // if
                } // for
                
            } // if

            if (keyInput.equals("W")) {
                for (int i = 0; i < context.rules.size(); i++) {
                    if (context.rules.get(i).isUsed) {
                        context.describeRuleAt(i);
                    } // if
                } // for
                
            } // if

            if (keyInput.equals("H")) {
                System.out.println("Input a parameter to analyze.");
                System.out.print("Expected Values: ");

                for (int i = 0; i < context.parameters.size(); i++) {
                    System.out.print(context.parameters.get(i).parameterName + " ");
                } // for

                System.out.println();
                System.out.print("$ ");

                String parameterToAnalyze = keyboard.next();

                for (int i = 0; i < context.rules.size(); i++) {
                    if (context.rules.get(i).isUsed && context.rules.get(i).conclusion.parameterToConclude.equals(parameterToAnalyze)) {
                        context.describeRuleAt(i);
                    } // if
                } // for
                
            } // if

            if (keyInput.equals("G")) {
                context.describeGoals();
                
            } // if

            if (keyInput.equals("B")) {
                // backchain stores the current backchain goal as well as subgoals in progress 
                Stack<Context.Goal> backChain = new Stack<Context.Goal>();

                // initialGoals stores a list of values given to be Goals from the initial input.
                Stack<Context.Goal> initialGoals = new Stack<Context.Goal>();

                // Push all goals in reverse order of appearance into the initialGoals stack (this is so that goals deeper in the list are added last)
                for (int i = context.parameters.size() - 1; i >= 0; i--) {
                    if (context.parameters.get(i).isAGoal == true) {
                        String currentGoalName = context.parameters.get(i).parameterName;
                        Context.Goal goal = context.new Goal(currentGoalName);
                        initialGoals.push(goal);
                    } // if
                } // if
                
                //Put the first goal into backChain
                backChain.push(initialGoals.pop());

                // Stores past goals and in reverse order of when the parameters were concluded
                Stack<Context.Goal> pastGoals = new Stack<Context.Goal>();

                while (!backChain.isEmpty()) {
                    if (stepByStepMode == true) {
                        System.out.println("Current Goal: " + backChain.peek().goalParameterName);
                    } // if

                    Context.Goal topGoal = backChain.peek();

                    //TODO
                    // Check if the current goal already has a presumed value. In which case, mark this and pop the current goal
                    // Otherwise, begin the rule search
                    boolean hasPresumedValue = false;

                    for (Parameter p : context.parameters) {
                        if (p.parameterName.equals(backChain.peek().goalParameterName) && !p.presumedValue.equals("")) {
                            hasPresumedValue = true;

                        } // if
                    } // if

                    // Search for rules that can conclude the current goal and evaluate them
                    // If returning from a concluded subgoal, continue from the rule that lead to the subgoal backchain
                    for (int i = backChain.peek().currentRuleIndex; i < context.rules.size() && hasPresumedValue == false; i++) {
                        RuleExpression currentRule = context.rules.get(i);

                        if (currentRule.conclusion.parameterToConclude.equals(topGoal.goalParameterName)) {
                            // Evaluate the rule's truth value; Null if backchain is required before it can be concluded.
                            Boolean conclusionValue = currentRule.evaluate(context);

                            if (stepByStepMode == true) {
                                if (conclusionValue == null) {
                                    System.out.println("Did we conclude the rule below? Inconclusive. Backchain needed.");
                                    context.describeRuleAt(i);
                                
                                } else if (conclusionValue.equals(Boolean.TRUE)) {
                                    System.out.println("Did we conclude the rule below? Yes.");
                                    context.describeRuleAt(i);
    
                                } // if
                            } // if

                            // If rule evaluation was inconclusive (returns null) due to a lack of information (a value that hasn't been given nor deduced), 
                            // suspend this parameter's search for now and backchain for the missing parameter by making it the new subgoal
                            if (conclusionValue == null) {
                                Context.Goal newSubGoal = context.new Goal(context.pushSubGoal);
                                topGoal.currentRuleIndex = i;

                                backChain.push(newSubGoal);
                                topGoal = backChain.peek();

                                if (stepByStepMode == true) {
                                    System.out.println("New Subgoal: " + newSubGoal.goalParameterName);
                                } // if

                                context.pushSubGoal = "";

                                //End rule search early to start from the beginning of the while loop with the new sub goal
                                i = context.rules.size();
                                

                            // If the rule evaluation returns true, add its conclusion's tally to the potential value it supports (+) or detracts (-) from
                            // Then, continue rule search. If rule evaluation concludes Boolean.FALSE simply skip to the next one.
                            } else if (conclusionValue.equals(Boolean.TRUE)) {
                                currentRule.isUsed = true;
                                topGoal.hasConclusion = true;

                                // Find the parameter
                                for (Parameter p : context.parameters) {
                                    if (p.parameterName.equals(currentRule.conclusion.parameterToConclude)) {
                                        // Find the potential value and add the conclusion's tally to it
                                        for (Parameter.PotentialValue pv : p.potentialValues) {
                                            if (pv.valueName.equals(currentRule.conclusion.parameterValue)) {
                                                // Add the conclusion tally to associated potentialValue
                                                if (stepByStepMode) {
                                                    System.out.println("Adding " + currentRule.conclusion.incrementTally + " to " + pv.valueName + "'s tally...");
                                                } // if
                                                
                                                pv.tally = pv.tally + currentRule.conclusion.incrementTally;
                                                
                                            } // if
                                        } // for
                                    } // if
                                } // for

                            } // if

                        } // if

                        //After evaluating every relevant rule...
                        if (i+1 == context.rules.size() && hasPresumedValue == false) {
                            //TODO
                            // if the goal parameter hasn't triggered any conclusions for true-valued rules,
                            // Then since screened for a presumed value earlier we can say definitively that the knowledge-base alone cannot deduce this parameter.
                            // Set its presumed value to unk (unknown). You might also ask the user to input the value if they have it.
                            // Pop backChain and push it into pastGoals
                            if (topGoal.hasConclusion == false) {
                                for (Parameter p : context.parameters) {
                                    if (p.parameterName.equals(backChain.peek().goalParameterName)) {
                                        p.presumedValue = "unk";

                                        
                                        if (stepByStepMode == true) {
                                            System.out.println(p.parameterName + " is unknown: Reached end of rule list.");
                                        } // if    
            
                                    } // if
                                } // for

                            // if rule has concluded by then, determine which potential value will become the presumed one. If none reach above 0, the presumed value is unk.
                            // Pop backChain and push it into pastGoals
                            } else {
                                if (stepByStepMode) {
                                    System.out.println("Determining which potential value will become the presumed one...");
                                } // if

                                //Find which potential value has the highest tally
                                for (Parameter p : context.parameters) {
                                    if (p.parameterName.equals(backChain.peek().goalParameterName)) {
                                        Parameter.PotentialValue bestPv = p.new PotentialValue();
                                        bestPv = p.potentialValues[0];
                                        
                                        // Match the potential values
                                        for (Parameter.PotentialValue pv : p.potentialValues) {
                                            if (bestPv.tally < pv.tally) {
                                                bestPv = pv;
                                                
                                            } // if    
                                        } // for

                                        if (bestPv.tally > 0) {
                                            p.presumedValue = bestPv.valueName;

                                            if (stepByStepMode == true) {
                                                System.out.println(p.parameterName + "'s presumed value is now " + p.presumedValue);
                                            } // if

                                        } else {
                                            p.presumedValue = "unk";

                                            if (stepByStepMode == true) {
                                                System.out.println(p.parameterName + " is unknown: No rule tallied more than 0.");
                                            } // if

                                        } // if
            
                                    } // if

                                } // for

                                // Pop the parameter from the subgoals and into the past subgoals
                                pastGoals.push(backChain.pop());
                                
                            } // if

                        } // if

                    } // for

                    if (hasPresumedValue == true) {
                        pastGoals.push(backChain.pop());

                    } // if

                    //TODO
                    // If backChain is empty, push the next initialGoal onto it as long as that goal hasn't already been finished while completing a pastGoal
                    if (backChain.isEmpty()) {                            
                        for (int i = 0; i < pastGoals.size(); i++) {
                            if (!initialGoals.isEmpty()) {
                                // See if the top initial goal has already been concluded for. If so, pop it.
                                if (initialGoals.peek().goalParameterName.equals(pastGoals.get(i).goalParameterName)) {
                                    initialGoals.pop();
                                } else {
                                    //Add one initial goal to backChain if it hasn't been investigated yet
                                    System.out.println("Backchain stack empty. Adding new initial goal... ");
                                    backChain.push(initialGoals.pop());
                                    i = pastGoals.size();
                                } // if
                            } // if
                        } // for

                    } // if

                    /*

                    //if (backChain.peek().goalParameterName.equals("Identity")) {
                    //    System.exit(0);
                    //} // if

                    // Search for rules that can conclude the current goal and evaluate them
                    // If returning from a concluded subgoal, continue from the rule that lead to the subgoal backchain
                    for (int i = backChain.peek().currentRuleIndex; i < context.rules.size(); i++) {
                        if (context.rules.get(i).conclusion.parameterToConclude.equals(backChain.peek().goalParameterName)) {
                            // Once a rule is found, evaluate its premises and see if you have all the necessary information
                            Boolean concludeThis = context.rules.get(i).evaluate(context);

                            if (stepByStepMode == true) {
                                if (concludeThis == null) {
                                    System.out.println("Did we conclude the rule below? Inconclusive. Backchain needed.");
                                    context.describeRuleAt(i);
                                
                                } else if (concludeThis.equals(Boolean.TRUE)) {
                                    System.out.println("Did we conclude the rule below? Yes.");
                                    context.describeRuleAt(i);

                                } // if
                            } // if

                            // If a presumed value of the hasn't been backchained for nor provided by the user, 
                            // push its parameter to the backChain list as a subgoal and search for it next
                            // concludeThis == null tells the current loop iteration that it should end and 
                            // start the while loop on the next subGoal pronto
                            if (concludeThis == null) {
                                Context.Goal newSubGoal = context.new Goal(context.pushSubGoal);
                                backChain.peek().currentRuleIndex = i;
                                backChain.push(newSubGoal);

                                if (stepByStepMode == true) {
                                    System.out.println("New Subgoal: " + newSubGoal.goalParameterName);
                                } // if

                                context.pushSubGoal = "";
                                i = context.rules.size();

                            // If the rule is satisfied, mark this and add the incrementTally of its Conclusion (representing its 
                            // likelihood over other answers and how confident the system is about its conclusion)
                            // to the total tally associated with its associated potential value
                            // Then continue to the next rule
                            } else if (concludeThis.equals(Boolean.TRUE)) {
                                context.rules.get(i).isUsed = true;
                                backChain.peek().hasConclusion = true;

                                // Match parameter
                                // Start by finding what index the parameter in question is
                                for (int j = 0; j < context.parameters.size(); j++) {
                                    // Match the parameter
                                    if (context.parameters.get(j).parameterName.equals(backChain.peek().goalParameterName)) {
                                        // Match the potential value
                                        for (int k = 0; k < context.parameters.get(j).potentialValues.length; k++) {
                                            if (context.parameters.get(j).potentialValues[k].valueName.equals(context.rules.get(i).conclusion.parameterValue)) {
                                                // Add the conclusion tally to associated potentialValue
                                                if (stepByStepMode) {
                                                    System.out.println("Adding " + context.rules.get(i).conclusion.incrementTally + " to " + context.parameters.get(j).potentialValues[k].valueName + "'s tally...");
                                                } // if
                                                
                                                context.parameters.get(j).potentialValues[k].tally = context.parameters.get(j).potentialValues[k].tally + context.rules.get(i).conclusion.incrementTally;
                                                
                                                k = context.parameters.get(j).potentialValues.length;

                                            } // if    
                                        } // for

                                        j =  context.parameters.size();    
                                    } // if

                                } // for                           

                                ///
                                //System.out.println("Using Rule " + i + " of " + context.rules.size());
                                context.rules.get(i).isUsed = true;
                                backChain.peek().hasConclusion = true;
                                //backChain.peek().currentRuleIndex = i;

                                for (int j = 0; j < context.parameters.size(); j++) {

                                    // Match the parameter
                                    if (context.parameters.get(j).parameterName.equals(backChain.peek().goalParameterName)) {

                                        // Match the potential value
                                        for (int k = 0; k < context.parameters.get(j).potentialValues.length; k++) {
                                            if (context.parameters.get(j).potentialValues[k].valueName.equals(context.rules.get(i).conclusion.parameterValue)) {
                                                //Add the tally from the Conclusion to it
                                                if (stepByStepMode) {
                                                    System.out.println("Adding " + context.rules.get(i).conclusion.incrementTally + " to " + context.parameters.get(j).potentialValues[k].valueName + "'s tally...");
                                                } // if

                                                context.parameters.get(j).potentialValues[k].tally = context.parameters.get(j).potentialValues[k].tally + context.rules.get(i).conclusion.incrementTally;
                                                
                                                k = context.parameters.get(j).potentialValues.length;
                                                //i = context.rules.size();
                                                //context.describeContext();
                                                //context.describeGoals();
                                                //System.exit(0);

                                            } // if    
                                        } // for

                                        j = context.parameters.size();

                                    } // if

                                } // for
                                
                                i = context.rules.size();

                                ////

                            } // if

                            // Once every rule that concludes a given parameter has been exhausted, it's time to
                            // Determine what potential value should become the presumed value 
                            // The highest tally value will become the presumed value 
                            if (i+1 == context.rules.size() && concludeThis != null) {
                                if (stepByStepMode) {
                                    System.out.println("Determining which potential value will become the presumed one...");
                                } // if

                                // Start by finding what index the parameter in question is
                                for (int j = 0; j < context.parameters.size(); j++) {
                                    // Match the parameter
                                    if (context.parameters.get(j).parameterName.equals(backChain.peek().goalParameterName)) {
                                        // Find which potential value has the highest tally
                                        Parameter.PotentialValue bestPv = context.parameters.get(j).new PotentialValue();
                                        bestPv = context.parameters.get(j).potentialValues[0];

                                        // Match the potential values
                                        for (int k = 1; k < context.parameters.get(j).potentialValues.length; k++) {
                                            if (bestPv.tally < context.parameters.get(j).potentialValues[k].tally) {
                                                bestPv = context.parameters.get(j).potentialValues[k];
                                                
                                            } // if    
                                        } // for

                                        // Once the most tallied value is found, make its value the presumed value of that parameter
                                        // If that value is > 0, make it the presumedValue. Otherwise, the presumed value is unknown.
                                        if (bestPv.tally > 0) {
                                            context.parameters.get(j).presumedValue = bestPv.valueName;

                                            if (stepByStepMode == true) {
                                                System.out.println(context.parameters.get(j).parameterName + "'s presumed value is now " + context.parameters.get(j).presumedValue);
                                            } // if

                                        } else {
                                            context.parameters.get(j).presumedValue = "unk";

                                            if (stepByStepMode == true) {
                                                System.out.println(context.parameters.get(j).parameterName + " is unknown. No rule tallied more than 0.");
                                            } // if
                                        } // if

                                        // Pop the parameter from the subgoals and into the past subgoals
                                        pastGoals.push(backChain.pop());
                                        j = context.parameters.size();
        
                                    } // if

                                } // for

                            } // if

                            // If after exhausting every rule no rule concluded on the parameter,
                            // Then the knowledgebase may be unable to deduce a presumed value for the parameter.
                            // If so, we can definitively say this parameter is unk/unknown
                        } else if (i+1 == context.rules.size() && backChain.peek().hasConclusion == false) {
                            for (int j = 0; j < context.parameters.size(); j++) {
                                //Match the parameter and check if rule actually has a given presumed value but simply had no rules that concluded its parameter
                                if (context.parameters.get(j).parameterName.equals(backChain.peek().goalParameterName) && !context.parameters.get(j).presumedValue.equals("")) {
                                    // Pop the parameter from the subgoals and into the past subgoals
                                    System.out.println("False alarm: The presumed value, " + context.parameters.get(j).presumedValue + ", of " + context.parameters.get(j).parameterName + " is already known.");
                                    
                                    // Pop the parameter from the subgoals and into the past subgoals
                                    pastGoals.push(backChain.pop());
                                    j = context.parameters.size();

                                } else {
                                    // Match the parameter
                                    if (context.parameters.get(j).parameterName.equals(backChain.peek().goalParameterName)) {
                                        // (here you might ask the user to manually input the value if they have it as a last ditch effort)
                                        context.parameters.get(j).presumedValue = "unk";

                                        if (stepByStepMode == true) {
                                            System.out.println(context.parameters.get(j).parameterName + " is unknown. Reached end of rule list.");
                                        } // if

                                        // Pop the parameter from the subgoals and into the past subgoals
                                        pastGoals.push(backChain.pop());

                                        j = context.parameters.size();
                                    } // if

                                } // if

                            } // for

                        } // if

                    } // for

                    // If the current backChain is empty, if one of the goals in this isn't already in pastGoals,
                    // Add it to backChain
                    if (backChain.isEmpty()) {                            
                        for (int i = 0; i < pastGoals.size(); i++) {
                            if (!initialGoals.isEmpty()) {
                                // See if the top initial goal has already been concluded for. If so, pop it.
                                if (initialGoals.peek().goalParameterName.equals(pastGoals.get(i).goalParameterName)) {
                                    initialGoals.pop();
                                } else {
                                    //Add one initial goal to backChain if it hasn't been investigated yet
                                    System.out.println("Backchain stack empty. Adding new initial goal... ");
                                    backChain.push(initialGoals.pop());
                                    i = pastGoals.size();
                                } // if
                            } // if
                        } // for

                    } // if

                } // while

                if (stepByStepMode) {
                    System.out.println("Past Goals (top is the most recently concluded): ");

                    for (int i = 0; i < pastGoals.size(); i++) {
                        System.out.println(" " + pastGoals.get(i).goalParameterName);
                    } // for   

                    System.out.println();
                } // if

                context.describeGoals();

                //Activate post-consultation options
                completedAConsultation = true;

            } // if

            */
                } // while

                System.out.println("\n~ Consultation Complete ~");
                //Once backChain is empty and there are no more initialGoals, go ahead and print the goal values we found during this consultation
                if (stepByStepMode) {
                    System.out.println("Past Goals (bottom is the most recently concluded): ");

                    for (int i = 0; i < pastGoals.size(); i++) {
                        System.out.println(" " + pastGoals.get(i).goalParameterName);
                    } // for   

                } // if

                context.describeGoals();

                //Activate post-consultation options
                completedAConsultation = true;

            } // if

        } // while 

        keyboard.close();

    } // main

} // JavaMYCIN

//Backchain Method based on stack
// Maybe make a class for "CurrentGoal", it has the Parameter name, concluded value, and the index of the rule expression that called for it. 
// Push the currentGoal onto the top of the stack and backchain for rules that conclude its parameter
// When all rules that can conclude that Goal are concluded...
//     set the highest tally potential value as the presumed input for the parameter
//     pop the current goal off the stack bc its value has been deduced
//     Jump to the ruleexpression index for the rule this backchain was started for in the first place, and evaluate it now that you have the requisite info

//Stack Helper Methods?

//Query Parameter Method

//Print Why

//Backchain Stack Class?




