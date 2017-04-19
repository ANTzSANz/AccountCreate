import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import ru.stqa.selenium.factory.WebDriverFactory;

public class IncomeCode {
	private static Logger log = Logger.getLogger(IncomeCode.class.getName());
	static WaitTimer timer = new WaitTimer();
	// ןמכÿ הכÿ האכüםוירוי נאבמעû
	private static String itemName = "Catit Design Senses Super Roller Circuit Toy for Cats";
	private static String itemAsin = "B00A4A7UOU";
	private static String itemGroup = "Pet Supplies > Cats > Toys > Mice & Animal Toys";
	private static String itemLink = "https://www.amazon.com/Catit-Design-Senses-Roller-Circuit/dp/B00A4A7UOU/ref=sr_1_59?s=pet-supplies&ie=UTF8&qid=1492620275&sr=1-59&keywords=cat+toys";
	private static String accLogin = "moira3348@mail.com";
	private String accPass = "121314";
	private static String keyWord = "cat toys";
	
	public static void main(String[] args) {

		runTheGame();

	}
	
    public static void runTheGame() {

		/*
		 * // timer.waitSeconds(30); log.log(Level.INFO,
		 * "Try to find stat data for item " + itemToMove.asin + " by key " +
		 * keyToMove.key); boolean itemNotFound = setStatDataForKey("", 0, 0,
		 * itemToMove.name, itemToMove.asin, keyToMove.key, itemToMove.group, 0,
		 * true);
		 * 
		 * if(!itemNotFound){ return; }
		 */

		int pause = getRandomNumber(120, 300); // set pause between mooving
		timer.waitSeconds(getRandomNumber(45, 90));
		for (int i = 0; i < 3; i++) {

			log.log(Level.INFO, "Item " + itemAsin + ". Try to move item.");

			String itemlinkToMove = itemLink;
			// check link and try to add item

			if (linkIsCorrect(itemLink)) {
				// add item to cart
				log.log(Level.INFO, "Item link for item " + itemAsin + " with key-words " + keyWord
						+ " was found. Try to move item.");
				timer.waitSeconds(5);

				moveItemOnAmazon(itemLink, itemName, itemAsin, accLogin, 0);

			} else {
				// continue;

				setStatDataForKey("", 0, 0, itemName, itemAsin, keyWord, itemGroup, 0, true);
				if (!linkIsCorrect(itemlinkToMove)) {
					log.log(Level.INFO, "Item link for item " + itemAsin + " with key-words " + keyWord
							+ " was not found before adding. Remove this key from adding list.");
					timer.waitSeconds(5);
					return;

				} else {

					// add item to cart
					moveItemOnAmazon(itemLink, itemName, itemAsin, accLogin, 0);

					// set info about adding
					System.out.println("All right!");

				}

			}

			// wait some time between adding
			timer.waitSeconds(pause);
		}

	}

	public static boolean setStatDataForKey(String searchLink, int page, int position, String itemName, String itemAsin,
			String keyWord, String group, int attempt, boolean itemNotFound) {
		// fields for exception
		int pageForEx = page;
		int positionForEx = position;
		int attemptForEx = attempt;
		String searchLinkForEx = searchLink;
		if (!itemNotFound) {
			return itemNotFound;
		}

		String mainGroup = "All Departments"; // getMainGroup(group);

		if (attempt == 1) {

			mainGroup = getMainGroup(group);
			log.log(Level.INFO,
					"Method setStatDataForKey. Item link wasn't found in 1 attempt. Try to find in group " + mainGroup);
		}
		if (attempt == 2) {

			if (group.contains(">")) {
				mainGroup = getMainGroup(group);
			} else {
				mainGroup = group;
			}

			searchLink = getSearchLinkByFilter(searchLink, itemName, itemAsin, keyWord, mainGroup);
			if (searchLink.isEmpty()) {
				log.log(Level.INFO, "Method setStatDataForKey. Search link with filters wasn't found. ");
				// set stat data if item not found (NF)
				attempt = 3;
				return itemNotFound;
			}
			log.log(Level.INFO, "Method setStatDataForKey. Try to find item in group " + mainGroup
					+ " by filter. New search link: \n " + searchLink);
		}
		if (attempt == 3) {
			log.log(Level.INFO, "Method setStatDataForKey. Item link with filters wasn't found in 3 attempts.");
			// set stat data if item not found (NF)

			String filter = "NOT FOUND";

			return itemNotFound;
		}
		if (attempt >= 4) {
			return itemNotFound;
		}
		String itemLink = "";
		attempt++;

		WebDriver userDriver = getProxyChromeDriver();

		// check captcha
		if (!userDriver.getPageSource().contains("searchDropdownBox")) {
			try {
				userDriver.quit();
			} catch (Exception e) {
				userDriver.quit();
			}
			timer.waitSeconds(getRandomNumber(600, 900));
			itemNotFound = setStatDataForKey(searchLinkForEx, pageForEx, positionForEx, itemName, itemAsin, keyWord,
					group, attemptForEx, itemNotFound);
		}

		if (searchLink.isEmpty() || searchLink.equals("")) {

			log.log(Level.INFO, "Method setStatDataForKey. Try to get new search for item.");

			if (userDriver.getPageSource().contains("searchDropdownBox")) {
				Select select = new Select(userDriver.findElement(By.id("searchDropdownBox")));
				select.selectByVisibleText(mainGroup);
				WebElement searchInput = userDriver.findElement(By.id("twotabsearchtextbox"));
				searchInput.sendKeys(keyWord);
				WebElement searchForm = userDriver.findElement(By.name("site-search"));
				timer.waitSeconds(getRandomNumber(5, 10));
				searchForm.submit();
				// get found items from amazon
				timer.waitSeconds(getRandomNumber(5, 10));
				searchLink = userDriver.getCurrentUrl();
				userDriver.get(searchLink);
				timer.waitSeconds(getRandomNumber(5, 10));
			}
		} else {
			userDriver.get(searchLink);
			timer.waitSeconds(getRandomNumber(5, 10));
		}
		// find last page
		int lastPage = 0;

		if (userDriver.getPageSource().contains("id=\"pagn\"")) {
			if (userDriver.getPageSource().contains("pagnDisabled")) {
				WebElement pagesElements = userDriver.findElement(By.id("pagn"));
				WebElement maxPageElement = pagesElements.findElement(By.className("pagnDisabled"));
				String lastPageStr = maxPageElement.getText();
				lastPage = Integer.valueOf(lastPageStr);
				log.log(Level.INFO, "Method setStatDataForKey. Last page was found. It is " + lastPage + " page.");
			} else {
				WebElement pagesElements = userDriver.findElement(By.id("pagn"));
				List<WebElement> pagesLinkElements = pagesElements.findElements(By.className("pagnLink"));
				if (!pagesLinkElements.isEmpty()) {
					WebElement lastPageElement = pagesLinkElements.get((pagesLinkElements.size() - 1));
					WebElement lastPageLink = lastPageElement.findElement(By.tagName("a"));
					String lastPageStr = lastPageLink.getText();
					lastPage = Integer.valueOf(lastPageStr);
				} else {
					lastPage = 1;
				}

				log.log(Level.INFO, "Method setStatDataForKey. Last page was found. It is " + lastPage + " page.");

			}
		}

		if (lastPage == 0) {
			lastPage = 1;
		}
		// lets start the game )))

		while (itemNotFound) {

			// check captcha
			if (!userDriver.getPageSource().contains("searchDropdownBox")) {
				try {
					userDriver.quit();
				} catch (Exception e) {
					userDriver.quit();
				}
				timer.waitSeconds(getRandomNumber(30, 90));
				setStatDataForKey(searchLinkForEx, pageForEx, positionForEx, itemName, itemAsin, keyWord, group,
						attemptForEx, itemNotFound);
			}
			String resultListID = "";
			if (userDriver.getPageSource().contains("s-results-list-atf")) {
				resultListID = "s-results-list-atf";
			} else {
				resultListID = "mainResults";
			}

			WebElement foundItemsList = userDriver.findElement(By.id(resultListID));
			List<WebElement> foundElements = foundItemsList.findElements(By.tagName("li"));
			if (!foundElements.isEmpty()) {
				page++;
				// log.log(Level.INFO, "Method setStatDataForKey. Try to find
				// item " + itemAsin + " on " + page + " page.");
				for (WebElement foundElement : foundElements) {
					String elementAsin = foundElement.getAttribute("data-asin");
					if (elementAsin != null) {
						position++;
						if (elementAsin.contains(itemAsin)) {

							List<WebElement> aElements = new ArrayList<>();
							aElements = foundElement.findElements(By.tagName("a"));
							itemLink = aElements.get(0).getAttribute("href");
							if (!itemLink.contains("https://www.amazon.com")) {
								itemLink = "https://www.amazon.com" + itemLink;
							}

							log.log(Level.INFO, "Method setStatDataForKey. Item was found on " + page + " page, "
									+ position + " position.\n" + "Link for item: " + itemLink);
							// set item info to DB

							String filter = "";
							if (attempt == 1) {
								filter = "not used";
							}
							if (attempt == 2) {
								filter = "in group";
							}
							if (attempt == 3) {
								filter = "by price";
							}

							System.out.println("Item found by " + filter);
							itemNotFound = false;
							attempt = 4;
							attemptForEx = 4;
							log.log(Level.INFO,
									"-----------------------------------------\nItem link for key was found! Try to return to main method to move item");
							try {
								userDriver.quit();
							} catch (Exception e) {
								log.log(Level.INFO,
										"-----------------------------------------\nDriver was closed with exception.");
							}
							return itemNotFound;
						}
					}
				}
			} else {
				try {
					userDriver.quit();
				} catch (Exception e) {
					log.log(Level.INFO, "-----------------------------------------\nDriver was closed with exception.");
					itemNotFound = setStatDataForKey("", 0, 0, itemName, itemAsin, keyWord, group, attempt,
							itemNotFound);
				}
				log.log(Level.INFO, "Method setStatDataForKey. Page was empty in attempt " + attempt);
				itemNotFound = setStatDataForKey("", 0, 0, itemName, itemAsin, keyWord, group, attempt, itemNotFound);
			}

			if (itemNotFound) {
				if (page < lastPage) {
					if (page == 1) {
						if (userDriver.getPageSource().contains("pagnNextLink")) {
							WebElement nextPageEl = userDriver.findElement(By.id("pagnNextLink"));
							searchLink = nextPageEl.getAttribute("href");
							if (!searchLink.contains("https://www.amazon.com")) {
								searchLink = "https://www.amazon.com" + searchLink;
							}
							userDriver.get(searchLink);
							timer.waitSeconds(getRandomNumber(4, 6)); // timer
						} else {
							log.log(Level.INFO,
									"Method setStatDataForKey. Search page doesn't have nextPage btn on page " + page
											+ " of " + lastPage + " pages. Try again.");
							timer.waitSeconds(getRandomNumber(5, 10));
							try {
								userDriver.quit();
								itemNotFound = setStatDataForKey("", 0, 0, itemName, itemAsin, keyWord, group, attempt,
										itemNotFound);
							} catch (Exception e) {
								log.log(Level.INFO,
										"-----------------------------------------\nDriver was closed with exception.");
								itemNotFound = setStatDataForKey("", 0, 0, itemName, itemAsin, keyWord, group, attempt,
										itemNotFound);
							}

						}
					} else {
						searchLink = getNextSearchPage(userDriver.getCurrentUrl(), page + 1);
						if (!searchLink.contains("https://www.amazon.com")) {
							searchLink = "https://www.amazon.com" + searchLink;
						}
						userDriver.get(searchLink);
						timer.waitSeconds(getRandomNumber(4, 6)); // timer
					}
				} else {
					log.log(Level.INFO, "Method setStatDataForKey. It's the last page. Attempt: " + attempt);
					try {
						userDriver.quit();
						itemNotFound = setStatDataForKey("", 0, 0, itemName, itemAsin, keyWord, group, attempt,
								itemNotFound);
					} catch (Exception e) {
						log.log(Level.INFO,
								"-----------------------------------------\nDriver was closed with exception.");
						itemNotFound = setStatDataForKey("", 0, 0, itemName, itemAsin, keyWord, group, attempt,
								itemNotFound);
					}
				}
			}
		}
		return itemNotFound;
	}

	public static void moveItemOnAmazon(String itemLink, String itemName, String itemAsin, String account,
			int atemptToLoginAcc) {

		atemptToLoginAcc++;
		// try to login
		WebDriver userDriver = getProxyChromeDriver();
		// check captcha
		if (!userDriver.getPageSource().contains("searchDropdownBox")) {
			try {
				userDriver.quit();
			} catch (Exception e) {
				userDriver.quit();
				timer.waitSeconds(getRandomNumber(60, 90));
				moveItemOnAmazon(itemLink, itemName, itemAsin, account, (atemptToLoginAcc - 1));
			}
			timer.waitSeconds(getRandomNumber(60, 90));
			moveItemOnAmazon(itemLink, itemName, itemAsin, account, (atemptToLoginAcc - 1));
		}
		timer.waitSeconds(getRandomNumber(10, 15));
		String startPageHTML = userDriver.getPageSource();
		timer.waitSeconds(getRandomNumber(10, 15));
		try {
			// check page isn't logined by any account
			if (startPageHTML.contains("Hello. Sign in")) {
				boolean accountNotLogined = true;
				while (accountNotLogined) {

					log.log(Level.INFO,
							"Method moveItemOnAmazon. Try to login " + account + " Attempt: " + atemptToLoginAcc);

					if (accountNotLogined) {
						WebElement regLinkElement = userDriver.findElement(By.id("nav-flyout-ya-signin"));
						WebElement regLinkAElement = regLinkElement.findElement(By.tagName("a"));
						String loginlink = regLinkAElement.getAttribute("href");
						if (!loginlink.contains("https://www.amazon.com")) {
							loginlink = "https://www.amazon.com" + loginlink;
						}
						userDriver.get(loginlink);
						timer.waitSeconds(getRandomNumber(10, 15));
						WebElement formElement = userDriver.findElement(By.name("signIn"));
						WebElement inputLogin = userDriver.findElement(By.id("ap_email"));
						inputLogin.sendKeys(account);
						WebElement inputPass = userDriver.findElement(By.id("ap_password"));
						inputPass.sendKeys("121314");
						timer.waitSeconds(getRandomNumber(40, 60));
						formElement.submit();
						timer.waitSeconds(getRandomNumber(10, 15));
						userDriver.get(itemLink);
						timer.waitSeconds(getRandomNumber(20, 25));
						String currentPage = userDriver.getPageSource();

						if (currentPage.contains("Hello, ")) {
							accountNotLogined = false;
							atemptToLoginAcc = 0;
							log.log(Level.INFO, "Method moveItemOnAmazon. Account " + account + " was logged in.");

						} else {
							log.log(Level.INFO, "Method moveItemOnAmazon. Account " + account
									+ " wasn't logined. Try again. Attempt " + atemptToLoginAcc);
							userDriver.quit();
							timer.waitSeconds(60);
							moveItemOnAmazon(itemLink, itemName, itemAsin, account, atemptToLoginAcc);
						}
					}
				}
			} else {
				log.log(Level.INFO, "Method moveItemOnAmazon. Account " + account
						+ " cannot be logined. Page has been already used.");
				userDriver.quit();
				timer.waitSeconds(30);
				moveItemOnAmazon(itemLink, itemName, itemAsin, account, atemptToLoginAcc);
			}

			// add to WL

			log.log(Level.INFO, "Method moveItemOnAmazon. Try to add item " + itemAsin + " to WL");
			String itemPage = userDriver.getPageSource();
			timer.waitSeconds(getRandomNumber(10, 20));
			String parentHandle = userDriver.getWindowHandle();

			if (itemPage.contains("id=\"wishListDropDown\"")) {
				if (itemPage.contains("add-to-wishlist-button")) {
					WebElement addToListBtn = userDriver.findElement(By.id("add-to-wishlist-button"));
					addToListBtn.click();
					timer.waitSeconds(getRandomNumber(7, 10));
					for (String childHandle : userDriver.getWindowHandles()) {
						if (!childHandle.equals(parentHandle)) {
							userDriver.switchTo().window(childHandle);
						}
					}
					itemPage = userDriver.getPageSource();

					if (itemPage.contains("atwl-dd-ul")) {
						WebElement ulListEl = userDriver.findElement(By.id("atwl-dd-ul"));
						timer.waitSeconds(getRandomNumber(7, 10));
						List<WebElement> liElements = ulListEl.findElements(By.tagName("li"));
						timer.waitSeconds(getRandomNumber(7, 10));
						for (WebElement liElement : liElements) {
							List<WebElement> spanElements = liElement.findElements(By.tagName("span"));
							timer.waitSeconds(getRandomNumber(7, 10));
							for (WebElement spanElement : spanElements) {
								timer.waitSeconds(getRandomNumber(7, 10));
								if (spanElement.getText().contains("Wish List")) {
									WebElement aElement = liElement.findElement(By.tagName("a"));
									aElement.click();
								}
							}
						}

						log.log(Level.INFO, "Method moveItemOnAmazon. Item " + itemName + " was added to WL.");
						timer.waitSeconds(getRandomNumber(7, 10));
					}
				}
			} else {
				if (userDriver.getPageSource().contains("add-to-wishlist-button-submit")) {
					WebElement addToListBtn = userDriver.findElement(By.id("add-to-wishlist-button-submit"));
					addToListBtn.click();
					timer.waitSeconds(getRandomNumber(7, 10));
					for (String childHandle : userDriver.getWindowHandles()) {
						if (!childHandle.equals(parentHandle)) {
							userDriver.switchTo().window(childHandle);
						}
					}
					itemPage = userDriver.getPageSource();
					if (itemPage.contains("WLNEW_newwl_section")) {
						WebElement wishSection = userDriver.findElement(By.id("WLNEW_newwl_section"));
						wishSection.click();
						timer.waitSeconds(getRandomNumber(7, 10));
						if (userDriver.getPageSource().contains("WLNEW_valid_submit")) {
							WebElement wishElementSubmit = userDriver.findElement(By.id("WLNEW_valid_submit"));
							wishElementSubmit.submit();
							log.log(Level.INFO, "Method moveItemOnAmazon. Item " + itemName + " was added to WL.");
						}
					}
				}

				timer.waitSeconds(getRandomNumber(7, 10));
			}

			userDriver.switchTo().window(parentHandle);

			timer.waitSeconds(getRandomNumber(10, 15));
			// add to cart
			userDriver.get(itemLink);
			log.log(Level.INFO, "Method moveItemOnAmazon. Try to add item " + itemAsin + " to cart");
			timer.waitSeconds(getRandomNumber(7, 15));
			if (userDriver.getPageSource().contains("add-to-cart-button")) {
				WebElement addToCartBtn = userDriver.findElement(By.id("add-to-cart-button"));
				addToCartBtn.submit();
				log.log(Level.INFO, "Method moveItemOnAmazon. Item " + itemName + " was added to cart.");
				timer.waitSeconds(getRandomNumber(10, 15));
				userDriver.quit();
				return;

			} else {
				log.log(Level.INFO, "Method moveItemOnAmazon. Navigation to item page wasn't made.");
				userDriver.quit();
				return;

			}
			// delete account from DB
			// AccountDAO.deleteAccount(account.login, account.password);

		} catch (Exception e) {
			log.log(Level.WARNING, "Method moveItemOnAmazon. Method has exception. Try again");
			userDriver.quit();
			timer.waitSeconds(getRandomNumber(180, 300));
			if (atemptToLoginAcc > 3) {
				log.log(Level.WARNING,
						"Method moveItemOnAmazon. Method has exception more 3 times. Item wasn't added!");
				return;
			} else {
				moveItemOnAmazon(itemLink, itemName, itemAsin, account, atemptToLoginAcc);
			}

		}
		userDriver.quit();
	}

	public void deleteFile(File file) {

		if (!file.exists()) {
			return;
		}
		File[] files = file.listFiles();
		if (files.length != 0) {
			for (File fileIn : files) {
				if (fileIn.isDirectory()) {
					for (File f : fileIn.listFiles()) {
						if (f.isDirectory()) {
							deleteFile(f);
							f.delete();
						} else {
							f.delete();
						}
					}
				}
				fileIn.delete();
			}
		}
	}

	/*
	 * private Account getAccountToMove() { boolean accountNotFound = true;
	 * Account accountForAdd = new Account(); while (accountNotFound) {
	 * 
	 * ArrayList<Account> accountsForAdd = new ArrayList<>(); try {
	 * accountsForAdd = AccountDAO.getAccountsForCartAdd(); log.log(Level.INFO,
	 * accountsForAdd.size() + " accounts were choosen for adding to cart."); }
	 * catch (DAOException e) { log.log(Level.INFO,
	 * "Getting accountsForCart has Exception. Try with new account");
	 * e.printStackTrace(); continue; }
	 * 
	 * // read account from file
	 * 
	 * if (accountsForAdd.size() == 0) { try { BufferedReader reader = new
	 * BufferedReader(new FileReader("C:\\java\\AccToDB.txt")); String line;
	 * while ((line = reader.readLine()) != null) { String[] splitedLine =
	 * line.split(" "); String accLogin = splitedLine[0]; String accPass =
	 * splitedLine[1]; AccountDAO.create(accLogin, accPass, 0); }
	 * reader.close(); accountsForAdd = AccountDAO.getAccountsForCartAdd(); }
	 * catch (NumberFormatException | IOException | DAOException e) {
	 * e.printStackTrace(); } } int accIndex =
	 * getRandomIndex(accountsForAdd.size()); accountForAdd =
	 * accountsForAdd.get(accIndex); log.log(Level.INFO, "AccountFC " +
	 * accountForAdd.login + " was choosen from list."); accountNotFound =
	 * false;
	 * 
	 * } log.log(Level.INFO, "AccountFC " + accountForAdd.login +
	 * " was found."); return accountForAdd; }
	 */
	/*
	 * public Account createNewAccount() {
	 * 
	 * Account account = new Account(); // get random account String accountName
	 * = getRandomAccName(); String accountEmail =
	 * getRandomAccEmail(accountName); String accountPassword = "246813579";
	 * 
	 * log.log(Level.INFO, "Method createNewAccount. Try to register account " +
	 * accountEmail); int atemptToCreate = 0; boolean accountIsNotCreated =
	 * true; while (accountIsNotCreated) { if (atemptToCreate > 3) {
	 * log.log(Level.INFO, "Method createNewAccount. Account " + accountEmail +
	 * " wasn't registered in 3 atempts. Try it with other one in 3 minutes.");
	 * timer.waitSeconds(3 * 60); createNewAccount(); } atemptToCreate++;
	 * log.log(Level.INFO, "Method createNewAccount. Try to register account " +
	 * accountEmail + " Atempt: " + atemptToCreate); WebDriver userDriver =
	 * getProxyFirefoxDriver();
	 * 
	 * try { String startPageHTML = userDriver.getPageSource();
	 * timer.waitSeconds(3); if (startPageHTML.contains("Hello. Sign in")) {
	 * 
	 * WebElement regLinkElement =
	 * userDriver.findElement(By.id("nav-flyout-ya-signin")); WebElement
	 * regLinkElementA = regLinkElement.findElement(By.tagName("a")); String
	 * loginlink = regLinkElementA.getAttribute("href"); if
	 * (!loginlink.contains("https://www.amazon.com")) { loginlink =
	 * "https://www.amazon.com" + loginlink; } userDriver.get(loginlink);
	 * timer.waitSeconds(getRandomNumber(5, 15)); WebElement newAccSubmit =
	 * userDriver.findElement(By.id("createAccountSubmit")); String reglink =
	 * newAccSubmit.getAttribute("href"); if
	 * (!reglink.contains("https://www.amazon.com")) { reglink =
	 * "https://www.amazon.com" + loginlink; } userDriver.get(reglink);
	 * timer.waitSeconds(getRandomNumber(5, 15)); // timer.waitGetAction();
	 * WebElement formElement =
	 * userDriver.findElement(By.id("ap_register_form")); WebElement nameElement
	 * = userDriver.findElement(By.id("ap_customer_name"));
	 * nameElement.sendKeys(accountName); WebElement emailElement =
	 * userDriver.findElement(By.id("ap_email"));
	 * emailElement.sendKeys(accountEmail); WebElement passwordElement =
	 * userDriver.findElement(By.id("ap_password"));
	 * passwordElement.sendKeys(accountPassword); WebElement
	 * checkPasswordElement =
	 * userDriver.findElement(By.id("ap_password_check"));
	 * checkPasswordElement.sendKeys(accountPassword); timer.waitGetAction();
	 * formElement.submit(); log.log(Level.INFO,
	 * "Method createNewAccount. Try to check registered account " +
	 * accountEmail); timer.waitSeconds(getRandomNumber(5, 15));
	 * userDriver.get("https://www.amazon.com");
	 * timer.waitSeconds(getRandomNumber(5, 15)); // check for new account if
	 * (userDriver.getPageSource().contains("Hello, " + accountName)) {
	 * accountIsNotCreated = false; account.login = accountEmail;
	 * account.password = accountPassword; account.used = 0;
	 * 
	 * userDriver.quit(); log.log(Level.INFO,
	 * "Method createNewAccount. Account " + account.login +
	 * " was registered and sent to other method."); return account; } else {
	 * log.log(Level.INFO,
	 * "Method createNewAccount. Account was not registered. Trying again.");
	 * 
	 * userDriver.quit(); timer.waitGetAction(); createNewAccount(); } } else {
	 * log.log(Level.INFO,
	 * "Method createNewAccount. Start page isn't loged out. Trying again.");
	 * 
	 * userDriver.quit(); timer.waitGetAction(); createNewAccount(); } } catch
	 * (Exception e) { log.log(Level.WARNING,
	 * "Method createNewAccount. Start page didn't load. Trying again.");
	 * 
	 * userDriver.quit(); timer.waitGetAction(); createNewAccount(); } }
	 * 
	 * if (account.login == null || account.login.isEmpty()) {
	 * log.log(Level.INFO, "Method createNewAccount. Account login is - " +
	 * account.login); createNewAccount(); } return account; }
	 */

	public static WebDriver getProxyChromeDriver() {
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\student.STEP\\Downloads\\geckodriver-v0.15.0-win64\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		timer.waitSeconds(5);
		driver.get("https://www.amazon.com");
		timer.waitSeconds(5);

		return driver;
	}

	public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	/*
	 * private WebDriver getProxyFirefoxDriver() {
	 * System.setProperty("webdriver.gecko.driver",
	 * "C:\\java\\selenium\\geckodriver.exe");
	 * 
	 * ProfilesIni WSP = new ProfilesIni(); FirefoxProfile profile =
	 * WSP.getProfile("amazon");
	 * 
	 * // set proxy ArrayList<ProxyForUse> proxies = getAllProxies();
	 * 
	 * int rndIndex = getRandomIndex(proxies.size());
	 * 
	 * ProxyForUse proxyForUse = proxies.get(rndIndex);
	 * 
	 * profile.setPreference("network.proxy.type", 1);
	 * profile.setPreference("network.proxy.http", proxyForUse.getProxy());
	 * profile.setPreference("network.proxy.http_port", proxyForUse.getPort());
	 * 
	 * try { ProxiesDAO.deleteProxy(proxyForUse.getProxy(),
	 * proxyForUse.getPort()); } catch (DAOException e1) { e1.printStackTrace();
	 * } // set other preferences
	 * profile.setPreference("permissions.default.image", 2);
	 * 
	 * DesiredCapabilities firefox = DesiredCapabilities.firefox();
	 * firefox.setCapability(FirefoxDriver.PROFILE, profile);
	 * 
	 * timer.waitSeconds(10); WebDriver driver =
	 * WebDriverFactory.getDriver(firefox);
	 * driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	 * driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS); try {
	 * timer.waitSeconds(15);
	 * 
	 * driver.get("https://www.amazon.com");
	 * 
	 * timer.waitSeconds(getRandomNumber(5, 10)); // it was 5-10 sec before
	 * 
	 * // check driver String currentPage = driver.getPageSource(); if
	 * (currentPage.contains("nav-flyout-ya-newCust")) { return driver; } else {
	 * driver.quit(); log.log(Level.INFO,
	 * "Method getNewFirefoxDriver. Web driver wasn't created. Try again");
	 * timer.waitSeconds(getRandomNumber(900, 1200)); // it was 15-20 // min //
	 * before getProxyFirefoxDriver(); }
	 * 
	 * } catch (Exception e) { log.log(Level.INFO,
	 * "Method getNewFirefoxDriver. Web driver wasn't created."); driver.quit();
	 * timer.waitSeconds(getRandomNumber(180, 300)); // it was 3-5 min // before
	 * 
	 * getProxyFirefoxDriver(); }
	 * 
	 * return driver; }
	 */

	public static String getSearchLinkByFilter(String searchLink, String itemName, String itemAsin, String keyWord,
			String group) {

		WebDriver userDriver = getProxyChromeDriver();
		log.log(Level.INFO, "Method getSearchLinkByFilter. Try to get new search for item by filter");
		try {
			if (userDriver.getPageSource().contains("searchDropdownBox")) {
				Select select = new Select(userDriver.findElement(By.id("searchDropdownBox")));
				select.selectByVisibleText(group);
				WebElement searchInput = userDriver.findElement(By.id("twotabsearchtextbox"));
				searchInput.sendKeys(keyWord);
				WebElement searchForm = userDriver.findElement(By.name("site-search"));
				timer.waitSeconds(getRandomNumber(5, 10));
				searchForm.submit();
				// get found items from amazon
				timer.waitSeconds(getRandomNumber(5, 10));
				String searchLinkGeneral = userDriver.getCurrentUrl();
				userDriver.get(searchLinkGeneral);
				timer.waitSeconds(getRandomNumber(5, 10));
				WebElement filterBlock = userDriver.findElement(By.id("refinements"));

				// set min and max walue for search
				int lowPriceInt = (2200 / 100) - 1;
				if (lowPriceInt < 0) {
					lowPriceInt = 0;
				}
				int highPriceInt = (2200 / 100) + 1;
				WebElement lowPriceIn = filterBlock.findElement(By.id("low-price"));
				lowPriceIn.sendKeys(String.valueOf(lowPriceInt));
				WebElement highPriceIn = filterBlock.findElement(By.id("high-price"));
				highPriceIn.sendKeys(String.valueOf(highPriceInt));
				timer.waitSeconds(getRandomNumber(5, 10));
				WebElement goBtn = filterBlock.findElement(By.className("leftNavGoBtn"));
				goBtn.click();
				timer.waitSeconds(getRandomNumber(5, 10));
				searchLink = userDriver.getCurrentUrl();
				userDriver.quit();
				return searchLink;
			}
		} catch (Exception e) {
			userDriver.quit();
			return "";
		}
		return searchLink;

	}

	public static String getNextSearchPage(String navUrl, int pageToBeIn) {
		String nextPage = "";
		if (!navUrl.contains("page=")) {
			log.log(Level.INFO, "Method getNextSearchPage. Source page cannot be changed.");
			return nextPage;
		}

		char[] strElements = navUrl.toCharArray();
		ArrayList<Character> charElements = new ArrayList<>();
		for (char chElement : strElements) {
			charElements.add(chElement);
		}
		char[] intElements = String.valueOf(pageToBeIn).toCharArray();
		int repIndex = -1;
		for (int i = 0; i < charElements.size(); i++) {
			if (charElements.get(i) == '=' && charElements.get(i - 1) == 'e' && charElements.get(i - 2) == 'g'
					&& charElements.get(i - 3) == 'a' && charElements.get(i - 4) == 'p') {
				repIndex = i + 1;
			}
			if (repIndex == i) {
				if (Character.isDigit(charElements.get(i))) {
					charElements.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < intElements.length; i++) {
			charElements.add(repIndex, intElements[i]);
			repIndex++;
		}
		for (char chToStr : charElements) {
			if (Character.isDigit(chToStr)) {
				nextPage += Character.getNumericValue(chToStr);
			} else {
				nextPage += String.valueOf(chToStr);
			}
		}
		return nextPage;
	}

	public static String getMainGroup(String group) {
		String mainGroup = "";
		char[] symbols = group.toCharArray();
		for (int i = 0; i < symbols.length; i++) {
			String iSymbol = Character.toString(symbols[i]);
			String nextSymbol = "";
			if (i != (symbols.length - 1)) {
				nextSymbol = Character.toString(symbols[i + 1]);
			}
			if (iSymbol.equals(" ") && nextSymbol.equals(">")) {
				return mainGroup;
			} else {
				mainGroup += iSymbol;
			}

		}
		return mainGroup;
	}

	/*
	 * private String getRandomAccEmail(String accountName) { int
	 * randomDomainIndex; // random case for domain choose randomDomainIndex = 1
	 * + (int) (Math.random() * (6 - 1)); String randomEmail = accountName;
	 * switch (randomDomainIndex) { case 1: randomEmail += "@gmail.com"; break;
	 * case 2: randomEmail += "@mail.com"; break; case 3: randomEmail +=
	 * "@hotmail.com"; break; case 4: randomEmail += "@bigmir.net"; break; case
	 * 5: randomEmail += "@zoho.eu"; break; default: randomEmail +=
	 * "@gmail.com"; break; } return randomEmail; }
	 * 
	 * private String getRandomAccName() { String randomName = getRandomName();
	 * 
	 * String allSymbols = "0123456789"; char[] symbols =
	 * allSymbols.toCharArray();
	 * 
	 * int randomLength = getRandomNumber(7, 12); for (int i = 0; i <
	 * randomLength; i++) { int index = (int) (Math.random() * symbols.length);
	 * randomName += Character.toString(symbols[index]); } return randomName; }
	 * 
	 * private String getRandomName() { String name = ""; ArrayList<String>
	 * names = new ArrayList<>(); names.add("Alex"); names.add("Ali");
	 * names.add("Artur"); names.add("Alla"); names.add("Allan");
	 * names.add("Anna"); names.add("Azur"); names.add("Archi");
	 * names.add("Boris"); names.add("Bogdan"); names.add("Borek");
	 * names.add("Egor"); names.add("Elena"); names.add("Alina");
	 * names.add("Eva"); names.add("Elsa"); names.add("Ekaterina");
	 * names.add("Georg"); names.add("Garik"); names.add("Gustaf");
	 * names.add("Evgen"); names.add("Leonid"); names.add("Michael");
	 * names.add("Marina"); names.add("Peter"); names.add("Mirabella");
	 * names.add("Maria"); names.add("Maks"); names.add("Maksim");
	 * names.add("Anastasia"); names.add("Nastya"); names.add("Nikita");
	 * names.add("Taras"); names.add("Teodor"); names.add("Timofey");
	 * names.add("Serg"); names.add("Sergio"); names.add("Oleg");
	 * names.add("Olga"); names.add("Andrey"); names.add("Victor");
	 * names.add("Victoria"); names.add("Vitaliy"); names.add("Vitalina");
	 * names.add("Vlad"); names.add("Valeria"); names.add("Valeriy");
	 * names.add("Ivan"); names.add("Roman"); names.add("Yana");
	 * names.add("Yarik"); names.add("Yaroslav"); names.add("Svetlana");
	 * names.add("Nataly"); names.add("Yan"); name =
	 * names.get(getRandomIndex(names.size())); return name; }
	 * 
	 */

	public int getRandomIndex(int size) {
		int index;
		index = (int) (Math.random() * size);
		// check that index is correct
		if (index > (size - 1)) {
			index = size - 1;
		}
		return index;
	}

	public static boolean linkIsCorrect(String itemLink) {
		boolean linkIsCorrect = false;
		if (!itemLink.isEmpty()) {
			linkIsCorrect = true;
			;
		}
		return linkIsCorrect;
	}

	public static int getRandomNumber(int min, int max) {
		int number = (int) (min + (Math.random() * (max - min)));
		return number;
	}
}
