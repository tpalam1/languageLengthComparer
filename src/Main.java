import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * In this project, I wanted to test the hypothesis that Spanish words in general are shorter than
 * French words.
 *
 * Let S = mean Spanish word length; F = mean French word length.
 *
 * Let H0: S = F; Ha = S < F.
 *
 * Given a corpus of Spanish and French text, determine if there is enough evidence to reject H0 at 95% confidence.
 *
 * The Spanish words are lyrics pulled from the following songs:
 *            (1) El Tren de Noé -- TéCanela
 *            (2) Milonga del Mar -- TOCH, Juanito
 *            (3) En Busca del Fuego -- Cuidad Jara
 *            (4) Mariposas -- Sangiovanni, Aitana
 *
 * The French words are lyrics pulled from the following songs:
 *            (1) Paris Montréal -- Cowboys Fringants
 *            (2) Ton visage -- Fréro Delavega
 *            (3) Première fois -- Rouquine
 */
public class Main {
  public static void main(String[] args) {

    // convert each corpus into a list of words
    ArrayList<String> spanishWordList = parseToWordList("./src/es_input.txt");
    ArrayList<String> frenchWordList = parseToWordList("./src/fr_input.txt");

    removeEmptyWords(spanishWordList);
    removeEmptyWords(frenchWordList);

    removePunctuation(spanishWordList);
    removePunctuation(frenchWordList);

    // convert each list of words into a list of double, representing word length
    ArrayList<Integer> spanishWordLengthList = parseToLengthList(spanishWordList);
    ArrayList<Integer> frenchWordLengthList = parseToLengthList(frenchWordList);

    removeZeros(spanishWordLengthList);
    removeZeros(frenchWordLengthList);

    System.out.println("Spanish list:\t" + spanishWordList + "\nFrench list:\t" + frenchWordList);

    ArrayList<Double> convertedListSpanish = convertToDoubleList(spanishWordLengthList);
    ArrayList<Double> convertedListFrench = convertToDoubleList(frenchWordLengthList);

    // get confidence intervals of both lists
    ArrayList<Double> spanishWordLengthCI = ConfidenceInterval.getConfidenceInterval(convertedListSpanish);
    ArrayList<Double> frenchWordLengthCI = ConfidenceInterval.getConfidenceInterval(convertedListFrench);

    System.out.println("Confidence interval for mean Spanish word length:\t" + spanishWordLengthCI);
    System.out.println("Confidence interval for mean French word length:\t" + frenchWordLengthCI);


    // conduct a hypothesis test on the
    // length of Spanish words being shorter than French.


    boolean results = conductHypothesisTest(spanishWordLengthCI, frenchWordLengthCI, HypothesisTest.LESS_THAN);

    System.out.println("The hypothesis test concludes that there is " + (!results ? "NO" : "") + " evidence that Spanish words tend to be shorter than French, at 95% confidence.");
  }

  /**
   * Removes all punctuation from the list of words given.
   *
   * @param wordList the list of words to parse.
   */
  private static void removePunctuation(ArrayList<String> wordList) {
    for(String string : wordList){
      string.replaceAll("\\p{Punct}", "");
    }
  }

  /**
   * Removes all zero-length words in the list.
   *
   * @param wordList the wordlist to parse.
   */
  private static void removeEmptyWords(ArrayList<String> wordList) {
    wordList.removeIf(String::isEmpty);
  }

  /**
   * Removes all zero values from the list.
   *
   * @param integerList integer list to parse.
   */
  private static void removeZeros(ArrayList<Integer> integerList) {
    integerList.removeIf(integer -> integer == 0);
  }

  /**
   * Conducts a hypothesis test on the given confidence intervals.
   *
   * @param confidenceIntervalX the first confidence interval to parse.
   * @param confidenceIntervalY the second confidence interval to parse.
   * @param hypothesisTest the type of hypothesis test to run; either:
   *                       (1) LESS_THAN, X < Y;
   *                       (2) GREATER_THAN, X > Y;
   *                       (3) NOT_EQUAL, X != Y.
   * @return the results of the hypothesis test.
   */
  private static boolean conductHypothesisTest(ArrayList<Double> confidenceIntervalX, ArrayList<Double> confidenceIntervalY, HypothesisTest hypothesisTest) {
    switch(hypothesisTest){
      case HypothesisTest.LESS_THAN -> {
        return checkIfXLessThanY(confidenceIntervalX, confidenceIntervalY);
      }
      case HypothesisTest.GREATER_THAN -> {
        return checkIfXGreaterThanY(confidenceIntervalX, confidenceIntervalY);
      }
      case HypothesisTest.NOT_EQUAL -> {
        return checkIfXLessThanY(confidenceIntervalX, confidenceIntervalY) || checkIfXGreaterThanY(confidenceIntervalX, confidenceIntervalY);
      }
    }

    throw new UnsupportedOperationException("Reached end of switch block.");
  }

  /**
   * Returns if the X interval is greater than the Y interval.
   * @param confidenceIntervalX the first confidence interval to parse.
   * @param confidenceIntervalY the second confidence interval to parse.
   * @return whether the X interval is greater than the Y interval.
   */
  private static boolean checkIfXGreaterThanY(ArrayList<Double> confidenceIntervalX, ArrayList<Double> confidenceIntervalY) {
    return confidenceIntervalX.getFirst() > confidenceIntervalY.getLast();
  }

  /**
   * Returns if the X interval is less than the Y interval.
   * @param confidenceIntervalX the first confidence interval to parse.
   * @param confidenceIntervalY the second confidence interval to parse.
   * @return whether the X interval is less than the Y interval.
   */
  private static boolean checkIfXLessThanY(ArrayList<Double> confidenceIntervalX, ArrayList<Double> confidenceIntervalY) {
    return confidenceIntervalX.getLast() < confidenceIntervalY.getFirst();
  }

  /**
   * Converts the given integer arraylist to a list of doubles.
   *
   * @param integerArrayList the list of integers to parse.
   * @return a list of doubles.
   */
  private static ArrayList<Double> convertToDoubleList(ArrayList<Integer> integerArrayList){
    ArrayList<Double> doubleArrayList = new ArrayList<>();

    for(Integer i : integerArrayList){
      doubleArrayList.add(i.doubleValue());
    }

    return doubleArrayList;
  }

  /**
   * Converts the given word list to a list of word lengths.
   *
   * @param wordList the list of words to parse.
   * @return a list of integers representing the word lengths.
   */
  private static ArrayList<Integer> parseToLengthList(ArrayList<String> wordList) {
    ArrayList<Integer> lengthList = new ArrayList<>();

    for(String string : wordList){
      lengthList.add(string.length());
    }

    return lengthList;
  }

  /**
   * Parses the given File path, if it exists, and returns all word tokens in the File.
   *
   * @param filepath the file path to parse.
   * @return a list of the words contained in the file.
   * @throws RuntimeException if the given file could not be found.
   */
  private static ArrayList<String> parseToWordList(String filepath){
    try
    {
      Scanner s = new Scanner(new File(filepath));

      ArrayList<String> output = new ArrayList<>();

      while(s.hasNext()){
        output.add(s.next());
      }

      return output;
    } catch(FileNotFoundException fileNotFoundException){
      throw new RuntimeException("The given filepath (" + filepath + ") could not be found.");
    }
  }
}