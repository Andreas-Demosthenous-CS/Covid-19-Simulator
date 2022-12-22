package ademos02.tk;

/**
 * This class is a library used fro running laboratory checks and other usefull 
 * tasks
 * 
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class VirusLab {

    /**
     * This method runs a virus test an the given patient 
     * and returns the result. Results are wrong based on a given probability
     * 
     * @param p the person to check
     * @param faultyResultProbability
     * @return
     */
    public static boolean checkPerson(Person p, int faultyResultProbability) {
        //If the person is vulnerable
        if (p.isVulnerable()) {
            //considering and simulating a probable mistake of the test
            boolean wrongResult = InfectionSimulator.getRandomResult(faultyResultProbability);
            //calculating probabilty
            //In case the result is wrong we mark the infected peron as healthy
            if(wrongResult && ((Vulnerable) p).isInfected()) return false;
            //Otherwise we mark the infected person as infected
            else return ((Vulnerable) p).isInfected();
        }
        return false;
    }

    /**
     * This method attempts to make a person severely sick
     * depending on his gender and age.
     * 
     * @param p
     * @return
     */
    public static boolean makeStronglySick(Vulnerable p) {

        if (!p.isInfected()) {
            return false;
        }

        int age = p.getAge();
        char gender = p.getGender();
        double probability = 0;

        //male
        if (gender == 'M') {
            if (age > 65) {
                probability = 35.00;
            } else if (age > 45) {
                probability = 20.00;
            } else if (age > 30) {
                probability = 5.50;
            } else if (age > 15) {
                probability = 0.05;
            } else {
                probability = 0.05;
            }
        }
        //female
        if (gender == 'F') {
            if (age > 65) {
                probability = 40.00;
            } else if (age > 45) {
                probability = 25.00;
            } else if (age > 30) {
                probability = 8.50;
            } else if (age > 15) {
                probability = 0.05;
            } else {
                probability = 0.05;
            }
        }
        return InfectionSimulator.getRandomResult(probability);
    }
    
    /**
     * Runs lab tests on an array of people
     * 
     * @param p the population to test
     * @param faultyResultProbability the probability of a faulty result
     */
    public static void runLabTest(Person p[], int faultyResultProbability){
        for(int i =0;i<p.length;i++){
            //Checking each person and setting informing it according to te test results
            if(checkPerson(p[i], faultyResultProbability)){
                //true -> infected
                //false -> healthy
                ((Vulnerable)p[i]).setKnowinglyInfected(true);
            }            
        }
    }

}
