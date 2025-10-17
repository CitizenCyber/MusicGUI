import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MusicPlaylistGUI extends JFrame {

    DefaultTableModel model;
    JTable table;
    JTextField userField, songField, artistField;
    JButton addButton, deleteButton;
    Connection connection;

    public MusicPlaylistGUI() {
        setTitle("Music Playlist System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "User", "Song", "Artist"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        userField = new JTextField();
        songField = new JTextField();
        artistField = new JTextField();

        inputPanel.add(new JLabel("User Name"));
        inputPanel.add(new JLabel("Song Title"));
        inputPanel.add(new JLabel("Artist"));

        inputPanel.add(userField);
        inputPanel.add(songField);
        inputPanel.add(artistField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Song");
        deleteButton = new JButton("Delete Selected");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/MusicDB", "root", "CmSmAhA@2023");

            System.out.println("Connection established");}
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
        }

        addButton.addActionListener(e -> {
            String user = userField.getText().trim();
            String song = songField.getText().trim();
            String artist = artistField.getText().trim();

            if (user.isEmpty() || song.isEmpty() || artist.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO Playlist (User, Song, Artist) VALUES (?, ?, ?)");
                stmt.setString(1, user);
                stmt.setString(2, song);
                stmt.setString(3, artist);
                stmt.executeUpdate();
                int nextId = model.getRowCount() + 1;
                model.addRow(new Object[]{nextId, user, song, artist});
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding song to database.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a row to delete.");
                return;
            }

            String user = model.getValueAt(selectedRow, 1).toString();
            String song = model.getValueAt(selectedRow, 2).toString();
            String artist = model.getValueAt(selectedRow, 3).toString();

            try {
                PreparedStatement stmt = connection.prepareStatement("DELETE FROM Playlist WHERE User = ? AND Song = ? AND Artist = ?");
                stmt.setString(1, user);
                stmt.setString(2, song);
                stmt.setString(3, artist);
                stmt.executeUpdate();
                model.removeRow(selectedRow);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting song from database.");
            }
        });

        setVisible(true);
    }

    private void clearFields() {
        userField.setText("");
        songField.setText("");
        artistField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MusicPlaylistGUI::new);
    }
}
