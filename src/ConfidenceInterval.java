import java.util.*;

/**
 * This class is used to generate 95% confidence intervals for
 * proportions and means.
 *
 * This is used for hypothesis testing.
 */
public final class ConfidenceInterval {
  /**
   * Returns the variance of the given array.
   */
  private static double getVariance(ArrayList<Double> arr){
    double var = 0;
    double mean = getMean(arr);
    for(Double d : arr){
      var += Math.pow(d - mean, 2);
    }
    return var;
  }

  /**
   * Returns the standard deviation of the given sample. Uses the Fisher correction.
   *
   * @throws java.lang.RuntimeException if the population is empty.
   */
  private static double getSampleStandardDeviation(ArrayList<Double> sample) {
    if(sample.isEmpty()){
      throw new RuntimeException("The sample size is zero!");
    }

    return Math.sqrt(getVariance(sample) / (sample.size() - 1));
  }

  /**
   * Returns the critical t-value for a two-tailed test.
   * Assumes two-tailed area is 5%.
   *
   * @param sampleSize the number of elements in the sample
   * @throws java.lang.RuntimeException if the sample size is less than 7.
   */
  public static double getCriticalTValue(int sampleSize){
    // Compute a rational approximation of the critical t-value
    // This approximation has RMSE = 0.048 over df = [1, 45].
    // This approximation gets better for larger df.

    final double b = -0.766593;
    final double c = 1.95996398454; // determines the asymptotic behavior of this function

    if(sampleSize < 7) {
      // For such a small sample, percent error exceeds 0.05.
      throw new RuntimeException("The sample size is less than 7: unable to get a reliable result for such a small sample.");
    } else if(sampleSize > 1000){
      // For such a large sample, return the critical z-value
      return c;
    }

    int degrees_of_freedom = sampleSize - 1;


    double numerator = degrees_of_freedom * (1 + c * degrees_of_freedom);
    double denominator = degrees_of_freedom * (b + degrees_of_freedom);

    return Math.abs(numerator / denominator);
  }

  /**
   * Calculates the standard error for a mean.
   * @param sampleStandardDeviation the standard deviation of the sample
   * @param sampleSize the number of sample observations
   * @return the standard error of the sample
   */
  private static double getMeanStandardError(double sampleStandardDeviation, int sampleSize){
    return sampleStandardDeviation / Math.sqrt(sampleSize);
  }

  /**
   * Calculates the margin of error for a sample mean.
   * @param sampleStandardDeviation the standard deviation of the sample
   * @param sampleSize the number of observations of the sample
   * @return the margin of error for the true population mean estimate, at 95% confidence.
   */
  private static double getMarginOfErrorMean(double sampleStandardDeviation, int sampleSize){
    double criticalTValue = getCriticalTValue(sampleSize);
    return criticalTValue * getMeanStandardError(sampleStandardDeviation, sampleSize);
  }

  /**
   * Returns the 95% confidence t-interval for the given sample mean.
   * @param sampleMean the sample mean
   * @param sampleSize the number of sample observations
   * @return the 95% CI for the population mean.
   */
  public static ArrayList<Double> getConfidenceInterval(double sampleMean, double sampleStandardDeviation, int sampleSize){
    ArrayList<Double> output = new ArrayList<>();

    double marginOfError = getMarginOfErrorMean(sampleStandardDeviation, sampleSize);

    double lowerBound = sampleMean - marginOfError;
    double upperBound = sampleMean + marginOfError;

    output.add(lowerBound); output.add(upperBound);

    return output;
  }

  /**
   * Returns the 95% confidence t-interval for the given sample's mean.
   * @param sample the sample for which to calculate a t-interval
   * @return the 95% CI for the population mean.
   */
  public static ArrayList<Double> getConfidenceInterval(ArrayList<Double> sample){
    double mean = getMean(sample);
    double sampleStandardDeviation = getSampleStandardDeviation(sample);

    return getConfidenceInterval(mean, sampleStandardDeviation, sample.size());
  }

  /**
   * Returns the mean of the given array.
   * @throws java.lang.RuntimeException if the given array is empty.
   */
  private static double getMean(ArrayList<Double> arr)
  {
    if(arr.isEmpty()){
      throw new RuntimeException("The given array is empty!");
    }
    return getSum(arr) / arr.size();
  }

  /**
   * Returns the sum of the given array.
   * @throws java.lang.RuntimeException if the given array is empty.
   */
  private static double getSum(ArrayList<Double> arr)
  {
    if(arr.isEmpty()){
      throw new RuntimeException("The given array is empty!");
    }
    double sum = 0;
    for(int i = 0; i < arr.size(); i++)
    {
      sum += arr.get(i);
    }
    return sum;
  }

  /**
   * Returns the 95% confidence z-interval for the given sample proportion.
   * @param sampleProportion the sample proportion
   * @param sampleSize the number of sample observations
   * @return the 95% CI for the population proportion.
   */
  public static ArrayList<Double> getConfidenceInterval(double sampleProportion, int sampleSize){
    ArrayList<Double> output = new ArrayList<>();

    double marginOfError = getMarginOfError(sampleProportion, sampleSize);

    double lowerBound = sampleProportion - marginOfError;
    double upperBound = sampleProportion + marginOfError;

    output.add(lowerBound); output.add(upperBound);

    return output;
  }

  /**
   * Checks whether the given confidence intervals have overlap.
   * If so, they can be said to be equal.
   */
  public static boolean hasOverlap(ArrayList<Double> confidenceInterval_1, ArrayList<Double> confidenceInterval_2){
    double lowerBound_1 = confidenceInterval_1.get(0);
    double upperBound_1 = confidenceInterval_1.get(1);

    double lowerBound_2 = confidenceInterval_2.get(0);
    double upperBound_2 = confidenceInterval_2.get(1);

    return (lowerBound_2 < upperBound_1 && upperBound_1 < upperBound_2) || (lowerBound_1 < upperBound_2 && upperBound_2 < upperBound_1);
  }

  /**
   * Calculates the margin of error for a sample of proportions.
   * @param sampleProportion the mean of the sample
   * @param sampleSize the number of observations of the sample
   * @return the margin of error for the true population proportion estimate, at 95% confidence.
   */
  private static double getMarginOfError(double sampleProportion, int sampleSize){
    return 1.96 * getStandardError(sampleProportion, sampleSize);
  }

  /**
   * Calculates the standard error for a proportion.
   * @param sampleProportion the mean of the sample
   * @param sampleSize the number of sample observations
   * @return the standard error of the sample
   */
  private static double getStandardError(double sampleProportion, int sampleSize){
    return Math.sqrt((sampleProportion * (1 - sampleProportion))/(double) sampleSize);
  }
}
