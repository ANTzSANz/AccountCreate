import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.WebDriver;

public class IncomeCodeTest {

	@Test
	public final void testSetStatDataForKey() {
		
	}

	@Test
	public final void testGetProxyChromeDriver() {
		WebDriver driver = IncomeCode.getProxyChromeDriver();
		String Url = driver.getCurrentUrl();
		driver.quit();
		assertTrue(Url.contains("amazon.com"));
	}

	@Test
	public final void testGetSearchLinkByFilter() {
		
		
	}

	@Test
	public final void testGetNextSearchPage() {
		
	}

	@Test
	public final void testGetMainGroup() {
		
	}

	@Test
	public final void testGetRandomIndex() {
		
	}

	@Test
	public final void testLinkIsCorrect() {
		
	}

	@Test
	public final void testGetRandomNumber() {
		
	}

}
