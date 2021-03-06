/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import data.Invite;
import data.Request;
import data.RequestKey;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 *
 * @author ADMIN
 */
public final class RoomPanel extends JPanel {

    private final Client client;
    private final JList<String> listName = new JList<>(new String[0]);
    private final JScrollPane scrollPane = new JScrollPane(listName);
    private final JButton buttonInvite = new JButton();
    private final JLabel labelSize = new JLabel();
    private final JTextField textFieldSize = new JTextField();
    private final JPanel panel = new JPanel();
    private Map<String, String> mapNameStatus = new LinkedHashMap<>(0);

    public RoomPanel(Client client) {
        this.client = client;
        listName.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listName.setLayoutOrientation(JList.VERTICAL);
        listName.setVisibleRowCount(-1);
        scrollPane.setPreferredSize(new Dimension(266, 175));
        buttonInvite.setText("Invite");
        buttonInvite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performInviteButtonAction();
            }
        });
        labelSize.setText("Preferred boad size:");
        textFieldSize.setColumns(3);
        panel.add(buttonInvite);
        panel.add(labelSize);
        panel.add(textFieldSize);

        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(3, 4, 1, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        add(scrollPane, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
        add(panel, gbc);
    }

    public void updateNameList(Map<String, String> map) {
        mapNameStatus = map;
        mapNameStatus.put(client.getName(), "(me)");
        DefaultListModel<String> dlm = new DefaultListModel<>();
        for (Map.Entry<String, String> entry : mapNameStatus.entrySet()) {
            String name = entry.getKey();
            String status = entry.getValue();
            dlm.addElement(name + " " + status);
        }
        listName.setModel(dlm);
    }

    public void showServerShutdownMessage() {
        JOptionPane.showMessageDialog(this, "Server shutdown");
    }

    public void showInviteCanceledMessage() {
        JOptionPane.showMessageDialog(this, client.getOpponentName() + " canceled the invite");
    }

    public void showInviteRejectedMessage() {
        JOptionPane.showMessageDialog(this, client.getOpponentName() + " rejected your invite");
    }

    public void showOpponentLeftRoomMessage() {
        JOptionPane.showMessageDialog(this, client.getOpponentName() + " left room");
    }

    public void showYouWinMessage() {
        JOptionPane.showMessageDialog(this, "You win");
    }

    public void showYouLoseMessage() {
        JOptionPane.showMessageDialog(this, "You lose");
    }

    public void showDrawMessage() {
        JOptionPane.showMessageDialog(this, "Draw");
    }

    private void performInviteButtonAction() {
        if (client.isBusy()) {
            return;
        }
        String selectedValue = listName.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(this, "Select a player to invite");
            return;
        }
        String selectedName = selectedValue.trim().split(" ")[0];
        String selectedStatus = mapNameStatus.get(selectedName);
        if (selectedStatus.equals("(me)") || selectedStatus.equals("(busy)")) {
            JOptionPane.showMessageDialog(this, "Select another player to invite");
            return;
        }
        int size;
        try {
            size = Integer.parseInt(textFieldSize.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid board size");
            return;
        }
        if (size < 5) {
            JOptionPane.showMessageDialog(this, "Minimum board size: 5");
            return;
        }
        if (size > 20) {
            JOptionPane.showMessageDialog(this, "Maximum board size: 20");
            return;
        }
        client.setBusy(true);
        Invite invite = new Invite(selectedName, size);
        Request request = new Request(RequestKey.SEND_INVITE, invite);
        client.getClientThread().sendRequestToServer(request);
        client.setOpponentName(selectedName);
        client.setSize(size);
        client.setInvitingFrame(new InvitingFrame(client));
        client.getInvitingFrame().setLocationRelativeTo(this);
        client.getInvitingFrame().setVisible(true);
    }
}
