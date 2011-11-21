
/***************************************************************************
 *   Copyright 2006-2011 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU General Public License as               *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with KouChat. If not, see <http://www.gnu.org/licenses/>.       *
 ***************************************************************************/

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.event.UserListListener;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.ui.ChatWindow;
import net.usikkert.kouchat.ui.UserInterface;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

/**
 * Implementation of a KouChat user interface that communicates with the Android GUI.
 *
 * @author Christian Ihle
 */
public class AndroidUserInterface implements UserInterface, ChatWindow, UserListListener {

    private final SpannableStringBuilder savedChat;
    private final MessageController msgController;
    private final Controller controller;
    private final UserList userList;

    private MainChatController mainChatController;

    public AndroidUserInterface() {
        savedChat = new SpannableStringBuilder();
        msgController = new MessageController(this, this);
        controller = new Controller(this);
        userList = controller.getUserList();
        userList.addUserListListener(this);
    }

    @Override
    public boolean askFileSave(final String user, final String fileName, final String size) {
        return false;
    }

    @Override
    public void showFileSave(final FileReceiver fileReceiver) {

    }

    @Override
    public void showTransfer(final FileReceiver fileRes) {

    }

    @Override
    public void showTransfer(final FileSender fileSend) {

    }

    @Override
    public void showTopic() {
        if (mainChatController != null) {
            final String nick = Settings.getSettings().getMe().getNick();
            final String topic = controller.getTopic().toString();

            mainChatController.updateTopic(nick + " - Topic: " + topic + " - " + Constants.APP_NAME);
        }
    }

    @Override
    public void clearChat() {

    }

    @Override
    public void changeAway(final boolean away) {

    }

    @Override
    public void notifyMessageArrived(final User user) {

    }

    @Override
    public void notifyPrivateMessageArrived(final User user) {

    }

    @Override
    public MessageController getMessageController() {
        return msgController;
    }

    @Override
    public void createPrivChat(final User user) {

    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void quit() {

    }

    @Override
    public void appendToChat(final String message, final int color) {
        final SpannableStringBuilder builder = new SpannableStringBuilder(message + "\n");
        builder.setSpan(new ForegroundColorSpan(color), 0, message.length(), 0);
        savedChat.append(builder);

        if (mainChatController != null) {
            mainChatController.appendToChat(message, color);
        }
    }

    public void logOn() {
        controller.logOn();
    }

    public void logOff() {
        controller.logOff(false);
    }

    public boolean isLoggedOn() {
        return controller.isLoggedOn();
    }

    public void registerMainChatController(final MainChatController mainChatController) {
        this.mainChatController = mainChatController;
        mainChatController.updateChat(savedChat);

        for (int i = 0; i < userList.size(); i++) {
            userAdded(i);
        }
    }

    public void unregisterMainChatController() {
        mainChatController = null;
    }

    public void sendMessage(final String message) {
        try {
            controller.sendChatMessage(message);
            msgController.showOwnMessage(message);
        }

        catch (final CommandException e) {
            msgController.showSystemMessage(e.getMessage());
        }
    }

    @Override
    public void userAdded(final int pos) {
        if (mainChatController != null) {
            final User user = userList.get(pos);
            mainChatController.addUser(user);
        }
    }

    @Override
    public void userChanged(final int pos) {
        if (mainChatController != null) {
            final User user = userList.get(pos);
            mainChatController.removeUser(user);
            mainChatController.addUser(user);
        }
    }

    @Override
    public void userRemoved(final int pos) {
        if (mainChatController != null) {
            final User user = userList.get(pos);
            mainChatController.removeUser(user);
        }
    }
}
