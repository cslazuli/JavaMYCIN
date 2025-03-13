## Getting Started

JavaMYCIN is a program written that can perform expert system consultations by parsing user-generated text files. It is modeled after [TMYCIN (“Tiny EMYCIN”) by Gordon S. Novak Jr.](https://www.cs.utexas.edu/~novak/tmycin/tmycin.html) of UTexas Austin which itself is modeled after EMYCIN. The provided contexts and rulesets in Snakes.txt and Rocks.txt are modeled after those provided in TMYCIN. JavaMYCIN and its accepted inputs have a distinct internal implementation from both of its predecessors, being written in Java.

To compile go to the project's root folder and run "javac -d bin .\src\my\project\javamycin\*.java" in your terminal

To run a consultation run java -cp bin my.project.javamycin.JavaMYCIN .\src\my\project\javamycin\resources\complete_context\\[Complete Context].txt .\src\my\project\javamycin\resources\input\\[Input].txt using the Complete Context and Input of your choice respectively.

## JavaMYCIN Overview

JavaMYCIN requires two input text files: 

The Complete Context file which includes the Context followed by a set of Rules. 

    Context: A list of Parameters that describe an individual and the different possible values for them.

    Rule: A statement that if an Individual is known to have some combination of parameter values (or unknown parameters), then we should conclude that it's likely or less likely to also have some other parameter value. Rule statements can be thought of as one or more predicates AND-ed together 
        
        e.g. "IF Parameter 1 has value A AND Parameter 2 has an unknown value and ... THEN conclude strongly that Parameter F has value E"

    Ruleset: A list of rules which altogether allow the expert system to make inferences about an Individual's unknown parameter values based on it's known ones.
    
An Individual file which represents an Individual within the Context known to have the user-provided parameter values.

## Writing Input Files

Formatting of CompleteContext.txt

    /DEFCONTEXT
    [Name of Context; e.g. Rocks, Snakes]

    [Parameter; e.g. Color] [Goal/NonGoal; Unfinished goal parameters will be added to the stack of parameters we're back chaining for if that stack is empty]
    [Question for the Parameter; e.g. What is the color of the rock?]
    [Potential values of that parameter; e.g. brown black white]

    [Define additional parameters as needed the same way]

    /DEFRULES

    /Rule
    [Parameter] [Parameter value; must be from the list of potential values]
    [Define more Parameter-Parameter value pairs as needed]
    Conclude [Parameter] [Value of this parameter to conclude] [Conclusion Strength Value]

    /Rule
    [Define the more rules using the same format as above]

Formatting of Individual.txt

    [Parameter] [Parameter value]
    [Continue list of given Parameter-Parameter Value pairs as needed]

Place your Complete Context Files inside of the .\src\my\project\javamycin\resources\complete_context folder.

Place your Input Files inside of the .\src\my\project\javamycin\resources\input folder.

## Writing Input Files (Advanced Tips)

Presumed unknown (unk) vs Inconclusive Parameters

    When parsing through rules, there are three possible conclusions: True (Conclude), False (Don't Conclude), and Inconclusive (Backchain from Here and try again).

    If there isn't enough information to conclude any single value for a Parameter, it will be assigned 'unk' to show it is presumed unknown.

    Rules can use 'unk' within them for example to represent situations where the lack evidence for a feature is indicative of a certain other feature.

Replicating OR and NOT

    Since Rules are made up of many simple judgement operations AND-ed together, certain operators like NOT, OR, and XOR require a bit of finnesse to mimic these features through the use of multiple rules together

    XOR
        The following example concludes if and only if the color of an igneous rock is known to be black OR purple

            /Rule
            Type igneous
            Color purple

            /Rule
            Type igneous
            Color black


    NOT
        Conclude upon the rule when it involves every other parameter value besides the excluded one, including 'unk' for unknown

        The following example concludes if and only if a pretty white rock is NOT known to be sedimentary

            /Rule
            Type metamorphic
            Color white
            Pretty yes

            /Rule
            Type igneous
            Color white
            Pretty yes

            /Rule
            Type unk
            Color white
            Pretty yes
        
Parameters with Multiple Values At Once
    
    Sometimes an individual has for multiple of the same parameter at once (e.g. a red, yellow, and black snake). If you anticipate this situation, you should include a parameter value with both values. You should also add a rule for the "both" parameter value for every rule involving their constituent parts (unless of course an individual with both features has distinct implications from one with only one or the other).

    Using this multiparameters, we can edit the XOR example above to mimic the behavior of OR.

        OR
        The following example concludes if and only if the color of an igneous rock is known to be black AND/OR purple

            /Rule
            Type igneous
            Color purple

            /Rule
            Type igneous
            Color black

            /Rule
            Type igneous
            Color black_and_purple
