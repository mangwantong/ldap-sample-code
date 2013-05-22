package samplecode.util;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Static data used by the {@code samplecode} packages.
 */
public final class StaticData {

  private StaticData() {
    // this block deliberately left empty
  }



  /**
   * provides access to the project resource bundle
   */
  public static final ResourceBundle getResourceBundle() {
    if (resourceBundle == null) {
      final String baseName = getResourceBundleBaseName();
      final Locale locale = getDefaultLocale();
      resourceBundle = ResourceBundle.getBundle(baseName,locale);
    }
    return resourceBundle;
  }



  /**
   * The base name of the resource bundle that contains resource data
   * used by the {@code samplecode} project.
   */
  public static final String getResourceBundleBaseName() {
    return StaticData.RESOURCE_BUNDLE_BASE_NAME;
  }



  /**
   * The default {@link Locale} when the Locale is not otherwise
   * specified
   */
  public static final Locale getDefaultLocale() {
    return StaticData.DEFAULT_LOCALE;
  }



  /**
   * The default {@link Locale} when the Locale is not otherwise
   * specified
   */
  public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

  /**
   * The base name of the resource bundle that contains resource data
   * used by the {@code samplecode} project.
   */
  public static final String RESOURCE_BUNDLE_BASE_NAME = "samplecode";

  // The resource bundle used by the sample code package
  private static ResourceBundle resourceBundle;

}
