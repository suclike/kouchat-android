
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.net.MessageResponderMock;
import net.usikkert.kouchat.util.TestClient;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests sending and receiving messages in the main chat.
 *
 * @author Christian Ihle
 */
public class MainChatTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;
    private int defaultOrientation;
    private TestClient client;

    public MainChatTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
        defaultOrientation = RobotiumTestUtils.getCurrentOrientation(solo);
    }

    public void test01OwnMessageIsShownInChat() {
        RobotiumTestUtils.writeLine(solo, "This is a new message from myself");
        solo.sleep(500);

        assertTrue(RobotiumTestUtils.searchText(solo, "This is a new message from myself"));
    }

    public void test02OwnMessageShouldArriveAtOtherClient() {
        client = new TestClient();
        final MessageResponderMock messageResponder = client.getMessageResponderMock();
        client.logon();

        RobotiumTestUtils.writeLine(solo, "This is the second message");
        solo.sleep(500);

        assertTrue(messageResponder.gotMessageArrived("This is the second message"));
    }

    public void test03OtherClientMessageIsShownInChat() {
        client = new TestClient();
        client.logon();

        client.sendChatMessage("Hello, this is a message from someone else");
        assertTrue(solo.searchText("Hello, this is a message from someone else"));
    }

    public void test04OrientationSwitchShouldKeepText() {
        assertTrue(solo.searchText("Welcome to KouChat"));

        RobotiumTestUtils.switchOrientation(solo);

        assertTrue(solo.searchText("Welcome to KouChat"));
    }

    public void test05OrientationSwitchShouldKeepLinks() {
        RobotiumTestUtils.writeLine(solo, "http://kouchat.googlecode.com/");

        solo.sleep(500);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus

        // The 2.3.3 emulator can't fit the whole url on a single line, so have to use a shorter text to locate
        RobotiumTestUtils.clickOnText(solo, "kouchat.googlecode.com");
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus

        solo.sleep(3000); // Close browser manually now!
        RobotiumTestUtils.switchOrientation(solo);

        solo.sleep(2000);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus
        RobotiumTestUtils.clickOnText(solo, "http://kouchat.googlecode.com/");
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus
    }

    // Must be verified manually
    public void test06OrientationSwitchShouldKeepSmileys() {
        RobotiumTestUtils.writeLine(solo, ":) :( :p :D ;) :O :@ :S ;( :$ 8)");

        solo.sleep(2000);
        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(2000);
    }

    // Must be verified manually. I haven't found an automated way to verify scrolling yet.
    public void test07OrientationSwitchShouldScrollToBottom() {
        for (int i = 1; i <= 30; i++) {
            RobotiumTestUtils.writeLine(solo,
                    "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! ");
        }

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(3000); // See if message number 30 is visible
    }

    public void test99Quit() {
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        if (client != null) {
            client.logoff();
        }

        RobotiumTestUtils.setOrientation(solo, defaultOrientation);
        solo.finishOpenedActivities();
    }
}
