import static java.lang.Math.toIntExact;

//package com.bham.pij.assignments.a2a;

public class Maths extends GAApplication {

    protected int goal;

    public Maths(int goal, int lengthOfExpression) {
        // Set size of population here
        population = new Individual[1000];
        // Set length of Individuals here
        lengthOfIndividual = lengthOfExpression;
        // Set target int (by default set to goal input) here
        this.goal = goal;
        // Set number of parents here
        numberOfParents = 40;
        // Set number of children here - REMARK: numberOfChildren mod numberOfParents must equal 0
        numberOfChildren = 40;
        // Set crossover probability here
        crossoverProbability = 0.8d;
        // Set mutation probability (to mutate a single gene) here
        mutationProbability = 0.03d;

        // Generates population for Maths
        generatePopulationMaths();
    }

    // Generates initial population with random values
    private void generatePopulationMaths() {
        for (int i = 0; i < population.length; i++) {
            population[i] = new Individual(lengthOfIndividual);
        }
    }

    // Assigns fitness values to all Individuals
    @Override
    protected void assignFitness() {
        for (int i = 0; i < population.length; i++) {
            // If valid mathematical expression
            if (isValid(i) == true) {
                // Set variable to result of the mathematical expression
                double individualFitness = calculate(population[i]);
                double distance = goal - individualFitness;

                // Set distance as always positive
                if (distance < 0) {
                    distance *= -1;
                }
                if (distance == 0) {
                    // Same fitness as the goal
                    population[i].setFitness(goal);
                } else {
                    // Relative fitness, gets smaller the further away from the goal
                    distance = goal / distance;
                    population[i].setFitness(distance);
                }
            } else {
                // Set fitness to high negative value
                population[i].setFitness(Integer.MAX_VALUE * -1);
            }
        }
    }

    // Checks if created term is mathematically valid (invalidates some mathematically valid terms for ease of use)
    private boolean isValid(int i) {
            boolean lastCharWasOperator = false;
            for (int j = 0; j < lengthOfIndividual; j++) {
                char currentChar = population[i].chromosome[j];

                // First sign - or + is allowed, * and / not
                if (j == 0) {
                    if (currentChar == '-' || currentChar == '+') {
                        lastCharWasOperator = true;
                    }
                    if (currentChar == '*' || currentChar == '/') {
                        return false;
                    }
                }

                // Any digits are always allowed
                if (Character.isDigit(currentChar) == true) {
                    lastCharWasOperator = false;
                }

                // Operator allowed if there was no operator at the previous char
                if (j != 0 && Character.isDigit(currentChar) == false) {
                    if (lastCharWasOperator == false) {
                        lastCharWasOperator = true;
                    } else {
                        return false;
                    }
                }

                // Last sign cannot be operator
                if (j + 1 == lengthOfIndividual && (currentChar == '-' || currentChar == '+' || currentChar == '*' || currentChar == '/')) {
                    return false;
                }
            }
            // If passing for-loop expression is valid
            return true;
    }

    // Create String array and set to empty Strings
    // Used to store adjacent digits together and to store operators for following calculation

    private String[] createMathExp(Individual individual) {
        String[] mathExp = new String[lengthOfIndividual];
        for (int j = 0; j < mathExp.length; j++) {
            mathExp[j] = "";
        }
        // Index to determine where to store the characters in the array
        int k = 0;
        for (int j = 0; j < lengthOfIndividual; j++) {
            // If digit, add to array
            if (Character.isDigit(individual.chromosome[j])) {
                mathExp[k] += Character.toString(individual.chromosome[j]);
            // If operator
            } else {
                // If first character, still add to array
                if (j == 0) {
                    mathExp[k] += Character.toString(individual.chromosome[j]);
                } else {
                    // Store operator in array at the next index -> k incremented
                    k++;
                    mathExp[k] = Character.toString(individual.chromosome[j]);
                    // Increment k again to write the next numbers in the following array index
                    k++;
                }
            }
        }
        return mathExp;
    }

    // Calculates result of created valid terms
    private double calculate(Individual individual) {

        String[] mathExp = createMathExp(individual);

        // Stores result to return
        double result = 0d;
        // Counts number of non-empty Strings in mathExp array for number of iterations of while loop (see below)
        int counter = 0;
        while (mathExp[counter] != "") {
            counter++;
        }
        // While more than one element of mathExp is not empty
        while (counter > 1) {


            // Perform all multiplications and divisions
            for (int j = 1; counter > 1 && j < mathExp.length; j++) {

                // Perform Multiplication

                // Use mathExp[j] != "" to prevent access of charAt(0) following if accessing empty Strings
                if (counter > 1 && mathExp[j] != "" && mathExp[j].charAt(0) == '*') {
                    int l = 1;
                    // Find non-empty left of * sign
                    while (mathExp[j - l] == "") {
                        l++;
                    }
                    // Find non-empty right of * sign
                    int r = 1;
                    while (mathExp[j + r] == "") {
                        r++;
                    }
                    // Calculate new value
                    long newValue = Long.parseLong(mathExp[j - l]) * Long.parseLong(mathExp[j + r]);
                    // Overwrite used numbers to prevent reuse
                    mathExp[j - l] = "";
                    mathExp[j + r] = "";
                    // Store at old position
                    mathExp[j] = Long.toString(newValue);
                    // Decrement counter twice (3 indexes used, one result number remains)
                    counter--;
                    counter--;
                    // If last non-empty String
                    if (counter == 1) {
                        // If in reasonable range of values
                        if (newValue < Integer.MAX_VALUE && newValue >= 0) {
                            result = (double) newValue;
                        } else {
                            // Otherwise set to max value
                            result = Integer.MAX_VALUE;
                        }
                        return result;
                    }
                }

                // Perform Division

                if (counter > 1 && mathExp[j] != "" && mathExp[j].charAt(0) == '/') {
                    int l = 1;
                    // Find non-empty left of / sign
                    while (mathExp[j - l] == "") {
                        l++;
                    }
                    // Find non-empty right of / sign
                    int r = 1;
                    while (mathExp[j + r] == "") {
                        r++;
                    }
                    // Detect Divison by 0
                    if (Long.parseLong(mathExp[j + r]) == 0) {
                        // Set fitness to worst possible
                        result = Integer.MAX_VALUE;
                        return result;
                    }
                    // Avoid Divison by 0

                    // Calculate new value
                    double newDoubleValue = Double.parseDouble(mathExp[j - l]) / Double.parseDouble(mathExp[j + r]);
                    long newValue;
                    // If divisible by 1 (integer value criteria)
                    if (newDoubleValue % 1 == 0) {
                        newValue = (long) newDoubleValue;
                    } else {
                        newValue = Integer.MAX_VALUE;
                    }
                    // Overwrite used numbers to prevent reuse
                    mathExp[j - l] = "";
                    mathExp[j + r] = "";
                    // Store at old position
                    mathExp[j] = Long.toString(newValue);
                    // Decrement counter twice (3 indexes used, one result number remains)
                    counter--;
                    counter--;
                    // If last non-empty String
                    if (counter == 1) {
                        // If in reasonable range of values
                        if (newValue < Integer.MAX_VALUE && newValue >= 0) {
                            result = (double) newValue;
                        } else {
                            // Otherwise set to max value
                            result = Integer.MAX_VALUE;
                        }
                        return result;
                    }

                }

            }


            // Afterwards Addition and Subtraction
            for (int j = 1; counter > 1 && j < mathExp.length; j++) {

                // Perform Addition

                if (counter > 1 && mathExp[j] != "" && mathExp[j].charAt(0) == '+') {
                    int l = 1;
                    // Find non-empty left of + sign
                    while (mathExp[j - l] == "") {
                        l++;
                    }
                    // Find non-empty right of + sign
                    int r = 1;
                    while (mathExp[j + r] == "") {
                        r++;
                    }
                    // Calculate new value
                    long newValue = Long.parseLong(mathExp[j - l]) + Long.parseLong(mathExp[j + r]);
                    // Overwrite used numbers to prevent reuse
                    mathExp[j - l] = "";
                    mathExp[j + r] = "";
                    // Store at old position
                    mathExp[j] = Long.toString(newValue);
                    // Decrement counter twice (3 indexes used, one result number remains)
                    counter--;
                    counter--;
                    // If last non-empty String
                    if (counter == 1) {
                        // If in reasonable range of values
                        if (newValue < Integer.MAX_VALUE && newValue >= 0) {
                            result = (double) newValue;
                        } else {
                            // Otherwise set to max value
                            result = Integer.MAX_VALUE;
                        }
                        return result;
                    }
                }

                // Perform Subtraction

                if (counter > 1 && mathExp[j] != "" && mathExp[j].charAt(0) == '-' && mathExp[j].length() == 1) {
                    int l = 1;
                    // Find non-empty left of - sign
                    while (mathExp[j - l] == "") {
                        l++;
                    }
                    // Find non-empty right of - sign
                    int r = 1;
                    while (mathExp[j + r] == "") {
                        r++;
                    }
                    // Calculate new value
                    long newValue = Long.parseLong(mathExp[j - l]) - Long.parseLong(mathExp[j + r]);
                    // Overwrite used numbers to prevent reuse
                    mathExp[j - l] = "";
                    mathExp[j + r] = "";
                    // Store at old position
                    mathExp[j] = Long.toString(newValue);
                    // Decrement counter twice (3 indexes used, one result number remains)
                    counter--;
                    counter--;
                    // If last non-empty String
                    if (counter == 1) {
                        // If in reasonable range of values
                        if (newValue < Integer.MAX_VALUE && newValue >= 0) {
                            result = (double) newValue;
                        } else {
                            // Otherwise set to max value
                            result = Integer.MAX_VALUE;
                        }
                        return result;
                    }
                }

            }
        }

        // If String has only one value (one long number)
        long newValue = Long.parseLong(mathExp[0]);
        if (newValue < Integer.MAX_VALUE && newValue >= 0) {
            result = toIntExact(newValue);
        } else {
            result = Integer.MAX_VALUE;
        }
        return result;
    }

    // Performs mutation for all genes of all Individuals (under consideration of mutationProbability)
    @Override
    protected void mutation() {
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < lengthOfIndividual; j++) {
                // Check if mutation for this gene is supposed to happen
                if (Math.random() <= mutationProbability) {
                    // Mutate this specific gene in Individual class
                    population[i].mutateGeneMaths(j);
                }
            }
        }
    }

    // Testing method to see performance of the algorithm
    public static void main (String[] args) {
        // Length of expression defined as 12 for assignment
        Maths maths = new Maths(123, 12);
        int j = 0;
        for (int i = 0; i < 1000 && maths.getBest().getFitness() != maths.goal; i++) {
            maths.run();
            j++;
        }
        System.out.println("Iteration number: " + j);
        System.out.println(maths.getBest().toString());
        System.out.println(maths.getBest().getFitness());
    }
}
