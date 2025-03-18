# Getting Started

JavaMYCIN is a shell program and library for performing expert system consultations through the use of user-generated text files. It is modeled after [TMYCIN (“Tiny EMYCIN”) by Gordon S. Novak Jr.](https://www.cs.utexas.edu/~novak/tmycin/tmycin.html) of UTexas Austin which itself is modeled after [EMYCIN](https://en.wikipedia.org/wiki/Mycin). The provided contexts and rulesets in Snakes.txt and Rocks.txt are modeled after those provided in TMYCIN. JavaMYCIN has a distinct internal implementation from both of its predecessors, being written in Java.

To compile go to the project's root folder and run `javac -d bin .\src\my\project\javamycin\*.java` in your terminal

To run a consultation run `java -cp bin my.project.javamycin.JavaMYCIN .\src\my\project\javamycin\resources\complete_context\Complete_Context.txt .\src\my\project\javamycin\resources\input\Input.txt` using the Complete Context and Input of your choice respectively.

# JavaMYCIN Overview

JavaMYCIN requires two input text files: 

The Complete Context file which includes the Context followed by a set of Rules. 

- Context: A list of Parameters that describe an individual and the different possible values for them.

- Rule: A statement that if an Individual is known to have some combination of parameter values (or unknown parameters), then we should conclude that it's likely or less likely to also have some other parameter value. Rule statements can be thought of as one or more predicates AND-ed together (e.g. "IF Parameter 1 has value A AND Parameter 2 has an unknown value and ... THEN conclude strongly that Parameter F has value E")

- Ruleset: A list of rules which altogether allow the expert system to make inferences about an Individual's unknown parameter values based on it's known ones through backchaining.
    
An Individual file which represents an Individual within the Context known to have the user-provided parameter values.

# Writing Input Files

Formatting of `CompleteContext.txt`

    /DEFCONTEXT
    [Name of Context; e.g. Rocks, Snakes]

    [Parameter; e.g. Color] [Goal/NonGoal; Unfinished goal parameters will be added to the stack of parameters we're back chaining for if that stack is empty]
    [Question for the Parameter; e.g. What is the color of the rock?]
    [Potential values of that parameter; e.g. brown black white]

    [Define additional parameters as needed by using the same format as above]

    /DEFRULES

    /Rule
    [Parameter] [Parameter value; must be from the list of potential values]
    [Define more Parameter-Parameter value pairs as needed]
    Conclude [Parameter] [Value of this parameter to conclude] [Conclusion Strength Value; This is a positive/negative integer. The specific value doesn't matter internally, so just consider it relative to other rules.]

    /Rule
    [Define more rules as needed by using the same format as above]

Formatting of `Individual.txt`

    [Parameter] [Parameter value]
    [Continue list of given Parameter-Parameter Value pairs as needed by using the same format as above.]

Place your Complete Context Files inside of the `.\src\my\project\javamycin\resources\complete_context` folder.

Place your Input Files inside of the `.\src\my\project\javamycin\resources\input` folder.

# Writing Input Files (Advanced Tips)

## Presumed unknown (unk) vs Inconclusive Parameters

When parsing through rules, there are three possible conclusions: True (Conclude), False (Don't Conclude), and Inconclusive (One of the parameters involved lacks a presumed value: Backchain from here and try again once we've deduced the value we were missing).

If there isn't enough evidence to conclude any single value of a sought Parameter over another, even after checking every rule that can conclude something about it, that Parameter's value will be assigned 'unk' to show it is presumed unknown.

Rules can use 'unk' within them for example to represent situations where the lack evidence for a feature is indicative of a certain other feature.

## Self-Referencing Rules

Self Referencing Rules are ones in which the Parameter being concluded upon is also involved in one of the rule's predicates.

In this example, if a rock is igneous and isn't already known to be pretty, the likelihood of that rock being pretty is increased greatly:
    
    /Rule
    Type igneous
    Pretty no
    Conclude Pretty yes 2000

    /Rule
    Type igneous
    Pretty unk
    Conclude Pretty yes 2000

Self Referencing Rules should be placed after every other rule that can conclude upon their parameter so that they get parsed parsed after every other rule.

## Replicating OR and NOT

Since Rules are made up of many simple judgement operations AND-ed together, certain operators like NOT, OR, and XOR require a little finnesse to mimic these operators by using multiple rules together.

### XOR
To replicate XOR, include rules that are identical except for having different values of the same parameter.

The following example concludes if and only if the color of an igneous rock is known to be exclusively black OR purple

    /Rule
    Type igneous
    Color purple
    Conclude ...

    /Rule
    Type igneous
    Color black
    Conclude ...

### NOT
To replicate NOT, provide identical rules that have the same conclusion for each potential value of a given parameter (including 'unk' for unknown) except for the excluded one.

The following example concludes if and only if a given pretty white rock is NOT known to be sedimentary

    /Rule
    Type metamorphic
    Color white
    Pretty yes
    Conclude ...

    /Rule
    Type igneous
    Color white
    Pretty yes
    Conclude ...

    /Rule
    Type unk
    Color white
    Pretty yes
    Conclude ...
        
## Parameters with Multiple Values At Once
    
Sometimes an Individual has multiple of the same parameter at once (e.g. a red, yellow, and black snake). If you anticipate this situation in your Context, you should include a parameter value representing multiple values at once. You should also add a rule for this multiparameter value for every rule involving its constituent parts (unless of course an Individual presenting with multiple features at once has distinct implications from one with only one of those features).

Using this multiparameters, we can edit the XOR example above to mimic the behavior of OR.

### OR
The following example concludes the same parameter value for each rule if and only if the color of an igneous rock is known to be black AND/OR purple

    /Rule
    Type igneous
    Color purple
    Conclude ...

    /Rule
    Type igneous
    Color black
    Conclude ...

    /Rule
    Type igneous
    Color black_and_purple
    Conclude ...
