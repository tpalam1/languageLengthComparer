import java.util.ArrayList;

/**
 * In this project, I wanted to test the hypothesis that Spanish words in general are shorter than
 * French words.
 */
public class Main {
  public static void main(String[] args) {

    // convert each corpus into a list of words
    ArrayList<String> spanishWordList = parseToWordList("./es_input.txt");
    ArrayList<String> frenchWordList = parseToWordList("./fr_input.txt");

    // convert each list of words into a list of double, representing word length
    ArrayList<Integer> spanishWordLengthList = parseToLengthList(spanishWordList);
    ArrayList<Integer> frenchWordLengthList = parseToLengthList(frenchWordList);

    // get confidence intervals of both lists
    ArrayList<Integer> spanishWordLengthCI = ConfidenceInterval.getConfidenceInterval(spanishWordLengthList);
    ArrayList<Integer> frenchWordLengthCI = ConfidenceInterval.getConfidenceInterval(frenchWordLengthList);

    // conduct a hypothesis test on the
    // length of Spanish words being shorter than French.
    conductHypothesisTest(spanishWordLengthCI, frenchWordLengthCI, HypothesisTest.LESS_THAN);
  }

  /**
   * Parses the given File path, if it exists, and returns all word tokens in the File.
   *
   * @param filepath the file path to parse.
   * @return
   */
  private ArrayList<String> parseToWordList(String filepath){
    Scanner s = new Scanner(filepath);
  }
}

/**
 * Denotes the 3 types of hypothesis tests possible.
 */
enum HypothesisTest{
  LESS_THAN, NOT_EQUAL, GREATER_THAN
}