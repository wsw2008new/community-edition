/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.ShareLinkPage;
import org.alfresco.po.share.site.document.VersionDetails;
import org.alfresco.po.share.site.document.ViewPublicLinkPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class AccessSharedDocumentTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AccessSharedDocumentTest.class);
    protected String testUser;
    protected String siteName = "";
    String domain = "invited.test";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14069() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14069() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        ShareUserSitePage.selectView(drone, ViewType.FILMSTRIP_VIEW);
        ShareUserSitePage.getFileDirectoryInfo(drone, fileName).selectThumbnail().render();

        FileDirectoryInfo fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);
        fileInfo1.clickInfoIcon();
        ShareLinkPage shareLinkPage = (ShareLinkPage) fileInfo1.clickShareLink().render();

        // The following information is displayed: Public link, Share with (Email, Facebook, Twitter, Google+), View, Unshare
        assertTrue(shareLinkPage.isEmailLinkPresent());
        assertTrue(shareLinkPage.isFaceBookLinkPresent());
        assertTrue(shareLinkPage.isTwitterLinkPresent());
        assertTrue(shareLinkPage.isGooglePlusLinkPresent());
        assertTrue(shareLinkPage.isUnShareLinkPresent());
        assertTrue(shareLinkPage.isViewLinkPresent());

        shareLinkPage.clickFaceBookLink();
        String mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Facebook"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        shareLinkPage.clickTwitterLink();
        mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Share a link on Twitter"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        shareLinkPage.clickGooglePlusLink();
        mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Google+"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        // Open the Public link
        ViewPublicLinkPage viewPublicLinkPage = shareLinkPage.clickViewButton().render();
        assertEquals(viewPublicLinkPage.getContentTitle(), fileName);
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14074() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14074() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();

        String shareUrl = shareLinkPage.getShareURL();

        ShareUser.logout(drone);

        drone.navigateTo(shareUrl);

        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify button login
        assertEquals(viewPublicLinkPage.getButtonName(), "Login");

        viewPublicLinkPage.clickOnDocumentDetailsButton();

        // Login Succeeds: When appropriate credentials
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // User2 is logged in successfully. Document Details page of the shared document is opened.
        // TODO: BUG ACE-3132 - Observed result: My dashboard page is opened.
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14075() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14075() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();

        String shareUrl = shareLinkPage.getShareURL();

        ShareUser.logout(drone);

        // Login as User2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // navigate to the shared link
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify that the page contains the document
        assertEquals(viewPublicLinkPage.getContentTitle(), fileName);
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

        // verify button Document Details
        assertEquals(viewPublicLinkPage.getButtonName(), "Document Details");

        // click on document details button
        DocumentDetailsPage detailsPage = viewPublicLinkPage.clickOnDocumentDetailsButton().render();
        assertEquals(detailsPage.getContentTitle(), fileName + "1.0");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14076() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_MODERATED);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14076() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();

        String shareUrl = shareLinkPage.getShareURL();

        ShareUser.logout(drone);

        // navigate to the shared link
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        String title = viewPublicLinkPage.getContentTitle();

        // verify that the page contains the document
        assertEquals(title, fileName);

        // verify button login
        assertEquals(viewPublicLinkPage.getButtonName(), "Login");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14077() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_MODERATED);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14077() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();

        String shareUrl = shareLinkPage.getShareURL();

        ShareUser.logout(drone);

        // User login user2.
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);
        // viewPublicLinkPage.render();

        // verify that the page contains the document
        assertEquals(viewPublicLinkPage.getContentTitle(), fileName);
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

        // verify button Document Details
        assertNotEquals(viewPublicLinkPage.getButtonName(), "Document Details");
        // verify button Login
        assertNotEquals(viewPublicLinkPage.getButtonName(), "Login");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14078() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14078() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();

        String shareUrl = shareLinkPage.getShareURL();

        ShareUser.logout(drone);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        String title = viewPublicLinkPage.getContentTitle();

        // verify that the page contains the document
        assertEquals(title, fileName);

        // verify button login
        assertEquals(viewPublicLinkPage.getButtonName(), "Login");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14079() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14079() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();

        String shareUrl = shareLinkPage.getShareURL();

        ShareUser.logout(drone);

        // User login user2.
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify that the page contains the document
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

        // verify button Document Details
        assertNotEquals(viewPublicLinkPage.getButtonName(), "Document Details");

        // verify button Login
        assertNotEquals(viewPublicLinkPage.getButtonName(), "Login");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14080() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14080() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();
        String shareUrl = shareLinkPage.getShareURL();

        // click unshare
        shareLinkPage.clickOnUnShareButton().render();

        // logout
        ShareUser.logout(drone);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify that the page contains the document
        assertFalse(viewPublicLinkPage.isDocumentViewDisplayed());

        String pageNotAvailable = viewPublicLinkPage.getPageNotAvailable();
        Assert.assertEquals(pageNotAvailable, "Page not available");

        Assert.assertEquals(viewPublicLinkPage.getBodyPageNotAvailable(), "It appears that this shared file link has been removed.");

        // verify button Document Details
        assertNotEquals(viewPublicLinkPage.getButtonName(), "Document Details");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14081() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14081() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();
        String shareUrl = shareLinkPage.getShareURL();

        // click unshare
        shareLinkPage.clickOnUnShareButton().render();

        // logout
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify that the page contains the document
        assertFalse(viewPublicLinkPage.isDocumentViewDisplayed());

        String pageNotAvailable = viewPublicLinkPage.getPageNotAvailable();
        Assert.assertEquals(pageNotAvailable, "Page not available");

        Assert.assertEquals(viewPublicLinkPage.getBodyPageNotAvailable(), "It appears that this shared file link has been removed.");

        // verify button Document Details
        assertNotEquals(viewPublicLinkPage.getButtonName(), "Document Details");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14082() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName + "1") + domain;
        String testUser2 = getUserNameFreeDomain(testName + "2") + domain;
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(testName + "modifed");
        contentDetails.setName(fileName);

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        // second edit
        contentDetails = new ContentDetails();
        contentDetails.setContent(testName + "modifed-2");
        contentDetails.setName(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        // User2 is invited to the site with Site Consumer role;
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.CONSUMER);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14082() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName + "1") + domain;
        String testUser2 = getUserNameFreeDomain(testName + "2") + domain;

        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();
        String shareUrl = shareLinkPage.getShareURL();

        // logout user 1
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify that the page contains the document
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

        // verify button Document Details
        assertEquals(viewPublicLinkPage.getButtonName(), "Document Details");

        // click on document details button
        DocumentDetailsPage detailsPage = viewPublicLinkPage.clickOnDocumentDetailsButton().render();
        List<String> documentActionsList = detailsPage.getDocumentActionList();
        List<String> expectedActions = new ArrayList<String>();
        if (!ShareUser.isAlfrescoVersionCloud(drone))
        {
            expectedActions.add("Download");
            expectedActions.add("View In Browser");
            expectedActions.add("Copy to...");
            expectedActions.add("Start Workflow");
            expectedActions.add("Publish");
            assertTrue(expectedActions.containsAll(documentActionsList));
        }
        else
        {
            expectedActions.add("Download");
            expectedActions.add("View In Browser");
            expectedActions.add("Copy to...");
            expectedActions.add("Create Task");
            assertTrue(expectedActions.containsAll(documentActionsList));
        }

        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();

        assertEquals(versionDetails.getVersionNumber(), "1.2", "Verifying version number");
        assertEquals(versionDetails.getFileName(), fileName, "Verifying File Name");

        assertTrue(detailsPage.isDownloadPreviousVersion("1.1"));

        assertTrue(detailsPage.isLikeLinkPresent());
        assertTrue(detailsPage.isFavouriteLinkPresent());

        // TODO:
        // - View Working Copy (for editing offline document)
        // - View original document (for editing offline document)
        // these options are not available for the invited user with role CONSUMER
        // Please modify TestLink step

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14083() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1") + domain;
        String testUser2 = getUserNameFreeDomain(testName + "2") + domain;
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(testName + "modifed");
        contentDetails.setName(fileName);

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        // second edit
        contentDetails = new ContentDetails();
        contentDetails.setContent(testName + "modifed-2");
        contentDetails.setName(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        // User2 is invited to the site with Site Consumer role;
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.CONTRIBUTOR);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14083() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1") + domain;
        String testUser2 = getUserNameFreeDomain(testName + "2") + domain;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();
        String shareUrl = shareLinkPage.getShareURL();

        // logout user 1
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify that the page contains the document
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

        // verify button Document Details
        assertEquals(viewPublicLinkPage.getButtonName(), "Document Details");

        // click on document details button
        DocumentDetailsPage detailsPage = viewPublicLinkPage.clickOnDocumentDetailsButton().render();
        List<String> documentActionsList = detailsPage.getDocumentActionList();
        List<String> expectedActions = new ArrayList<String>();
        if (!ShareUser.isAlfrescoVersionCloud(drone))
        {
            expectedActions.add("Download");
            expectedActions.add("View In Browser");
            expectedActions.add("Copy to...");
            expectedActions.add("Start Workflow");
            expectedActions.add("Publish");
            assertTrue(expectedActions.containsAll(documentActionsList));
        }
        else
        {
            expectedActions.add("Download");
            expectedActions.add("View In Browser");
            expectedActions.add("Copy to...");
            expectedActions.add("Create Task");
            assertTrue(expectedActions.containsAll(documentActionsList));
        }

        assertTrue(detailsPage.isLikeLinkPresent());
        assertTrue(detailsPage.isFavouriteLinkPresent());
        assertTrue(detailsPage.isAddCommentButtonPresent());

        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();

        assertEquals(versionDetails.getVersionNumber(), "1.2", "Verifying version number");
        assertEquals(versionDetails.getFileName(), fileName, "Verifying File Name");
        assertTrue(detailsPage.isDownloadPreviousVersion("1.1"));

        // TODO:
        // - View Working Copy (for editing offline document)
        // - View original document (for editing offline document)
        // these options are not available for the invited user with role CONTRIBUTOR
        // Please modify TestLink step

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14084() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1") + domain;
        String testUser2 = getUserNameFreeDomain(testName + "2") + domain;
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(testName + "modifed");
        contentDetails.setName(fileName);

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        // second edit
        contentDetails = new ContentDetails();
        contentDetails.setContent(testName + "modifed-2");
        contentDetails.setName(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        // User2 is invited to the site with Site Consumer role;
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14084() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1") + domain;
        String testUser2 = getUserNameFreeDomain(testName + "2") + domain;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();
        String shareUrl = shareLinkPage.getShareURL();

        // logout user 1
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify that the page contains the document
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

        // verify button Document Details
        assertEquals(viewPublicLinkPage.getButtonName(), "Document Details");

        // click on document details button
        DocumentDetailsPage detailsPage = viewPublicLinkPage.clickOnDocumentDetailsButton().render();
        List<String> documentActionsList = detailsPage.getDocumentActionList();
        List<String> expectedActions = new ArrayList<String>();
        
        if (!ShareUser.isAlfrescoVersionCloud(drone))
        {
            expectedActions.add("Download");
            expectedActions.add("View In Browser");
            expectedActions.add("Copy to...");
            expectedActions.add("Start Workflow");
            expectedActions.add("Publish");
            expectedActions.add("Edit Properties");
            expectedActions.add("Upload New Version");
            expectedActions.add("Inline Edit");
            expectedActions.add("Edit Offline");
            expectedActions.add("Edit in Google Docs™");
            expectedActions.add("Manage Aspects");
            expectedActions.add("Change Type");
            expectedActions.add("Email as link");

            if (ShareUser.isHybridEnabled())
            {
                expectedActions.add("Sync to Cloud");
            }

            assertTrue(expectedActions.containsAll(documentActionsList));
        }
        else
        {
            expectedActions.add("Download");
            expectedActions.add("View In Browser");
            expectedActions.add("Copy to...");
            expectedActions.add("Publish");
            expectedActions.add("Edit Properties");
            expectedActions.add("Upload New Version");
            expectedActions.add("Inline Edit");
            expectedActions.add("Edit Offline");
            expectedActions.add("Edit in Google Docs™");
            expectedActions.add("Create Task");
            assertTrue(expectedActions.containsAll(documentActionsList));
        }

        assertTrue(detailsPage.isLikeLinkPresent());
        assertTrue(detailsPage.isFavouriteLinkPresent());
        assertTrue(detailsPage.isAddCommentButtonPresent());

        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();

        assertEquals(versionDetails.getVersionNumber(), "1.2", "Verifying version number");
        assertEquals(versionDetails.getFileName(), fileName, "Verifying File Name");

        assertTrue(detailsPage.isDownloadPreviousVersion("1.1"));

        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FileDirectoryInfo fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);

        // edit offline document
        fileInfoDir.selectEditOfflineAndCloseFileWindow().render();
        detailsPage = docLib.selectFile(fileName).render();

        assertTrue(detailsPage.isViewOriginalLinkPresent());
        detailsPage.selectViewOriginalDocument().render();
        assertTrue(detailsPage.isViewWorkingCopyDisplayed());
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14085() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1") + domain;
        String testUser2 = getUserNameFreeDomain(testName + "2") + domain;
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(testName + "modifed");
        contentDetails.setName(fileName);

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        // second edit
        contentDetails = new ContentDetails();
        contentDetails.setContent(testName + "modifed-2");
        contentDetails.setName(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        // User2 is invited to the site with Site Consumer role;
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.MANAGER);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14085() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1") + domain;
        String testUser2 = getUserNameFreeDomain(testName + "2") + domain;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();
        String shareUrl = shareLinkPage.getShareURL();

        // logout user 1
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // navigate to the shared link
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // verify that the page contains the document
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

        // verify button Document Details
        assertEquals(viewPublicLinkPage.getButtonName(), "Document Details");

        // click on document details button
        DocumentDetailsPage detailsPage = viewPublicLinkPage.clickOnDocumentDetailsButton().render();
        List<String> documentActionsList = detailsPage.getDocumentActionList();
        List<String> expectedActions = new ArrayList<String>();

        if (!ShareUser.isAlfrescoVersionCloud(drone))
        {
            expectedActions.add("Download");
            expectedActions.add("View In Browser");
            expectedActions.add("Copy to...");
            expectedActions.add("Start Workflow");
            expectedActions.add("Publish");
            expectedActions.add("Edit Properties");
            expectedActions.add("Upload New Version");
            expectedActions.add("Inline Edit");
            expectedActions.add("Edit Offline");
            expectedActions.add("Edit in Google Docs™");
            expectedActions.add("Manage Aspects");
            expectedActions.add("Change Type");
            expectedActions.add("Move to...");
            expectedActions.add("Delete Document");
            expectedActions.add("Manage Permissions");

            if (ShareUser.isHybridEnabled())
            {
                expectedActions.add("Sync to Cloud");
            }

            assertTrue(expectedActions.containsAll(documentActionsList));
        }
        else
        {
            expectedActions.add("Download");
            expectedActions.add("View In Browser");
            expectedActions.add("Copy to...");
            expectedActions.add("Edit Properties");
            expectedActions.add("Upload New Version");
            expectedActions.add("Inline Edit");
            expectedActions.add("Edit Offline");
            expectedActions.add("Edit in Google Docs™");
            expectedActions.add("Change Type");
            expectedActions.add("Move to...");
            expectedActions.add("Delete Document");
            expectedActions.add("Manage Permissions");
            expectedActions.add("Create Task");
            assertTrue(expectedActions.containsAll(documentActionsList));
        }

        assertTrue(expectedActions.containsAll(documentActionsList));

        assertTrue(detailsPage.isLikeLinkPresent());
        assertTrue(detailsPage.isFavouriteLinkPresent());
        assertTrue(detailsPage.isAddCommentButtonPresent());

        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();

        assertEquals(versionDetails.getVersionNumber(), "1.2", "Verifying version number");
        assertEquals(versionDetails.getFileName(), fileName, "Verifying File Name");

        assertTrue(detailsPage.isDownloadPreviousVersion("1.1"));

        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FileDirectoryInfo fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);

        // edit offline document
        fileInfoDir.selectEditOfflineAndCloseFileWindow().render();
        detailsPage = docLib.selectFile(fileName).render();

        assertTrue(detailsPage.isViewOriginalLinkPresent());
        detailsPage.selectViewOriginalDocument().render();
        assertTrue(detailsPage.isViewWorkingCopyDisplayed());
    }

}
