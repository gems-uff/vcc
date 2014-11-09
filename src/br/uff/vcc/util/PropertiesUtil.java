package br.uff.vcc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

	private static final String configsFilePath = "c:\\VCC\\config.properties";
	private static FileInputStream fis;

	private static Properties loadProperties() throws IOException {
		Properties prop = new Properties();
		fis = new FileInputStream(configsFilePath);
		prop.load(fis);

		return prop;
	}

	public static final double readMinimumSupport() {
		try {
			Properties prop = loadProperties();

			double minimumSupport = Double.parseDouble((String) prop.get("minimumSupport"));
			if (minimumSupport > 0 && minimumSupport <= 1)
				return minimumSupport;
			else
				throw new Exception(
						"Minimum support should be contined between 0 and 1 ");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The minimum support couldn't be read, using default support of 15%");
			return 0.15;
		} finally {
			closeProperties();
		}
	}

	private static void closeProperties() {
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Integer readMaxSizeCombinations() {
		try {
			Properties prop = loadProperties();
			int maxSizeCombinations = Integer.parseInt((String) prop.get("maxCombinationSize"));
			if (maxSizeCombinations > 0)
				return maxSizeCombinations;
			else
				throw new Exception(
						"Max Size Combinations should be greater than 0");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Max Size Combinations couldn't be read, using default value of 10");
			return 10;
		} finally {
			closeProperties();
		}
	}

	public static Boolean readUseLastMethodAllSuggestionQueries() {
		try {
			Properties prop = loadProperties();
			Boolean useLastMethodAllSuggestionQueries = Boolean.parseBoolean((String) prop.get("useLastMethodAllSuggestionQueries"));
			return useLastMethodAllSuggestionQueries;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Use Last Method All Suggestion Queries couldn't be read, using default value of false");
			return Boolean.FALSE;
		} finally {
			closeProperties();
		}
	}
}
