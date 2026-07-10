package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.PlattformManager;
import model.Buchung;
import model.Parkplatz;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;


public class KundenDashboardView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color PAGE_BG = new Color(245, 247, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color PRIMARY_DARK = new Color(67, 56, 202);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color SUCCESS_BG = new Color(238, 242, 255);
    private static final Color TABLE_HEADER_BG = new Color(239, 243, 255);
    private static final Color TABLE_ROW_ALT = new Color(249, 250, 255);

    private final PlattformManager manager;

    private JTable tblParkplaetze;
    private JTextField txtSuchOrt;
    private DatePicker txtVonDatum;
    private JTextField txtVonUhrzeit;
    private DatePicker txtBisDatum;
    private JTextField txtBisUhrzeit;
    private JButton btnSuchen;
    private JButton btnBuchen;
    private JButton btnJetztSetzen;
    private JButton btnPreisakktualisieren;
    private JLabel lblPreis;
    private JLabel lblHinweis;
    private java.awt.CardLayout kundenSeitenUmschalter;
    private JPanel kundenSeitenContainer;
    private JTable tblMeineBuchungen;
    private DefaultTableModel modellMeineBuchungen;
    private JButton btnZurueckZurSuche;

    public KundenDashboardView(PlattformManager pm) {
        this.manager = pm;

        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(14, 14, 14, 14));
        setBackground(PAGE_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        kundenSeitenUmschalter = new java.awt.CardLayout();
        kundenSeitenContainer = new JPanel(kundenSeitenUmschalter);
        kundenSeitenContainer.setOpaque(false);

        JPanel suchSeite = createMainPanel();
        JPanel meineBuchungenSeite = createMeineBuchungenSeite();

        kundenSeitenContainer.add(suchSeite, "SUCHEN");
        kundenSeitenContainer.add(meineBuchungenSeite, "MEINE_BUCHUNGEN");

        add(kundenSeitenContainer, BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setOpaque(false);

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);

        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.gridy = 0;
        topGbc.fill = GridBagConstraints.BOTH;
        topGbc.weighty = 0;

        topGbc.gridx = 0;
        topGbc.weightx = 0.82;
        topGbc.insets = new Insets(0, 0, 0, 14);
        topPanel.add(createSearchCard(), topGbc);

        topGbc.gridx = 1;
        topGbc.weightx = 1.68;
        topGbc.insets = new Insets(0, 0, 0, 0);
        topPanel.add(createTimeCard(), topGbc);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 14));
        centerWrapper.setOpaque(false);
        centerWrapper.add(createTableCard(), BorderLayout.CENTER);
        centerWrapper.add(createPriceBookingPanel(), BorderLayout.SOUTH);

        root.add(topPanel, BorderLayout.NORTH);
        root.add(centerWrapper, BorderLayout.CENTER);

        btnSuchen.addActionListener(e -> parkplaetzeSuchen());
        btnBuchen.addActionListener(e -> buchungAusfuehren());
        btnJetztSetzen.addActionListener(e -> aktuelleZeitSetzen());
        btnPreisakktualisieren.addActionListener(e -> preisAktualisieren());

        tblParkplaetze.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preisAktualisieren();
            }
        });

        return root;
    }
    
    private JPanel createMeineBuchungenSeite() {
        JPanel seite = new JPanel(new BorderLayout(14, 14));
        seite.setOpaque(false);
        seite.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel kopf = new JPanel(new BorderLayout());
        kopf.setOpaque(false);

        JLabel titel = new JLabel("Meine gebuchten Parkplätze");
        titel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titel.setForeground(TEXT_DARK);
        kopf.add(titel, BorderLayout.WEST);

        btnZurueckZurSuche = new GradientButton("Neue Buchung");
        btnZurueckZurSuche.setPreferredSize(new Dimension(210, 44));
        kopf.add(btnZurueckZurSuche, BorderLayout.EAST);
        btnZurueckZurSuche.addActionListener(e -> kundenSeitenUmschalter.show(kundenSeitenContainer, "SUCHEN"));

        seite.add(kopf, BorderLayout.NORTH);

        modellMeineBuchungen = new DefaultTableModel(
            new Object[]{"Buchungscode", "Parkplatz", "Von", "Bis", "Preis (€)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tblMeineBuchungen = new JTable(modellMeineBuchungen);
        tblMeineBuchungen.setRowHeight(32);
        tblMeineBuchungen.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblMeineBuchungen.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tblMeineBuchungen.getTableHeader().setBackground(TABLE_HEADER_BG);

        JScrollPane scroll = new JScrollPane(tblMeineBuchungen);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        seite.add(scroll, BorderLayout.CENTER);

        return seite;
    }
    
    private void aktualisiereMeineBuchungen() {
        modellMeineBuchungen.setRowCount(0);

        if (!(manager.getAktuellerNutzer() instanceof model.Kunde)) {
            return;
        }

        model.Kunde kunde = (model.Kunde) manager.getAktuellerNutzer();

        for (Buchung b : kunde.getMeineBuchungen()) {
            modellMeineBuchungen.addRow(new Object[]{
                b.getBuchungsCode(),
                b.getParkplatz().getBezeichnung(),
                b.getVon(),
                b.getBis(),
                String.format("%.2f", b.berechnePreis())
            });
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new GradientPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 24, 20, 24));
        header.setPreferredSize(new Dimension(0, 110));

        JLabel lblTitel = new JLabel("Kunden-Dashboard");
        lblTitel.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitel.setForeground(Color.WHITE);

        JLabel lblUntertitel = new JLabel("Finde Parkplätze, wähle deinen Zeitraum und buche in wenigen Klicks.");
        lblUntertitel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblUntertitel.setForeground(new Color(235, 240, 255));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(lblTitel);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(lblUntertitel);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createSearchCard() {
        JPanel card = createCardPanel("Standortsuche");
        card.setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = baseGbc();

        txtSuchOrt = createRoundedTextField();
        txtSuchOrt.setPreferredSize(new Dimension(220, 44));

        btnSuchen = new GradientButton("Suchen");
        btnSuchen.setPreferredSize(new Dimension(130, 44));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        content.add(createFieldLabel("Ort oder Adresse"), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(6, 6, 6, 10);
        content.add(txtSuchOrt, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(6, 0, 6, 6);
        content.add(btnSuchen, gbc);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTimeCard() {
        JPanel card = createCardPanel("Zeitraum");
        card.setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        txtVonDatum = createLayoutDatePicker();
        txtVonUhrzeit = createRoundedTextField();
        txtBisDatum = createLayoutDatePicker();
        txtBisUhrzeit = createRoundedTextField();

        txtVonDatum.setPreferredSize(new Dimension(160, 44));
        txtVonUhrzeit.setPreferredSize(new Dimension(135, 44));
        txtBisDatum.setPreferredSize(new Dimension(160, 44));
        txtBisUhrzeit.setPreferredSize(new Dimension(135, 44));

        btnJetztSetzen = new GradientButton("Mindestzeit ab jetzt setzen");
        btnJetztSetzen.setPreferredSize(new Dimension(210, 42));
        

        GridBagConstraints gbc = baseGbc();
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        content.add(createFieldLabel("Von Datum"), gbc);

        gbc.gridx = 1;
        content.add(createFieldLabel("Von Uhrzeit"), gbc);

        gbc.gridx = 2;
        content.add(createFieldLabel("Bis Datum"), gbc);

        gbc.gridx = 3;
        content.add(createFieldLabel("Bis Uhrzeit"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        content.add(txtVonDatum, gbc);

        gbc.gridx = 1;
        content.add(txtVonUhrzeit, gbc);

        gbc.gridx = 2;
        content.add(txtBisDatum, gbc);

        gbc.gridx = 3;
        content.add(txtBisUhrzeit, gbc);

        lblHinweis = new JLabel("Format: Datum = dd.MM.yyyy, Uhrzeit = HH:mm");
        lblHinweis.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHinweis.setForeground(TEXT_MUTED);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 6, 4, 6);
        content.add(lblHinweis, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        content.add(btnJetztSetzen, gbc);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableCard() {
        JPanel card = createCardPanel("Zu buchende Parkplätze");
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(0, 320));

        tblParkplaetze = new JTable();
        tblParkplaetze.setModel(new DefaultTableModel(
                new Object[]{"ID", "Bezeichnung", "Adresse", "Kapazität", "Stundensatz (€)"}, 0
        ));
        tblParkplaetze.setRowHeight(32);
        tblParkplaetze.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblParkplaetze.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblParkplaetze.setGridColor(new Color(235, 238, 245));
        tblParkplaetze.setShowVerticalLines(false);
        tblParkplaetze.setShowHorizontalLines(true);
        tblParkplaetze.setIntercellSpacing(new Dimension(0, 1));
        tblParkplaetze.setSelectionBackground(new Color(224, 231, 255));
        tblParkplaetze.setSelectionForeground(TEXT_DARK);
        tblParkplaetze.setFillsViewportHeight(true);

        tblParkplaetze.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tblParkplaetze.getTableHeader().setBackground(TABLE_HEADER_BG);
        tblParkplaetze.getTableHeader().setForeground(TEXT_DARK);
        tblParkplaetze.getTableHeader().setReorderingAllowed(false);

        applyTableRenderers();

        JLabel lblInfo = new JLabel("Wähle einen Parkplatz aus der Liste aus, damit der Preis berechnet werden kann.");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblInfo.setForeground(TEXT_MUTED);

        JScrollPane scroll = new JScrollPane(tblParkplaetze);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(0, 220));

        JPanel inner = new JPanel(new BorderLayout(0, 8));
        inner.setOpaque(false);
        inner.add(lblInfo, BorderLayout.NORTH);
        inner.add(scroll, BorderLayout.CENTER);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPriceBookingPanel() {
        JPanel card = createPlainRoundedCard();
        card.setLayout(new BorderLayout(16, 8));
        card.setPreferredSize(new Dimension(0, 120));

        JPanel top = new JPanel(new BorderLayout(16, 0));
        top.setOpaque(false);

        lblPreis = new JLabel("Preis: -");
        lblPreis.setOpaque(true);
        lblPreis.setBackground(SUCCESS_BG);
        lblPreis.setForeground(PRIMARY_DARK);
        lblPreis.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblPreis.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(199, 210, 254), 20, 1),
                new EmptyBorder(12, 18, 12, 18)
        ));

        btnBuchen = new GradientButton("Jetzt buchen");
        btnBuchen.setPreferredSize(new Dimension(210, 52));
        
        btnPreisakktualisieren = new GradientButton("Preis aktualisieren");
        btnPreisakktualisieren.setPreferredSize(new Dimension(190, 42));

        top.add(lblPreis, BorderLayout.CENTER);
        top.add(btnBuchen, BorderLayout.EAST);
        top.add(btnPreisakktualisieren, BorderLayout.WEST);

        JLabel info = new JLabel("Parkplatz auswählen, Zeitraum prüfen und direkt buchen.");
        info.setForeground(TEXT_MUTED);
        info.setFont(new Font("SansSerif", Font.PLAIN, 12));
        info.setBorder(new EmptyBorder(2, 2, 0, 2));

        card.add(top, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    private void applyTableRenderers() {
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALT);
                    c.setForeground(TEXT_DARK);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALT);
                    c.setForeground(TEXT_DARK);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        tblParkplaetze.getColumnModel().getColumn(0).setCellRenderer(centeredRenderer);
        tblParkplaetze.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        tblParkplaetze.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        tblParkplaetze.getColumnModel().getColumn(3).setCellRenderer(centeredRenderer);
        tblParkplaetze.getColumnModel().getColumn(4).setCellRenderer(centeredRenderer);
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(createRoundedTitledBorder(title));
        return card;
    }

    private JPanel createPlainRoundedCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 22, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return card;
    }

    private Border createRoundedTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 22, 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createEmptyBorder(),
                                title,
                                0,
                                0,
                                new Font("SansSerif", Font.BOLD, 14),
                                TEXT_DARK
                        ),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JTextField createRoundedTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(PRIMARY_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(203, 213, 225), 18, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    
    private DatePicker createLayoutDatePicker() {
    	DatePickerSettings settings = new DatePickerSettings();
        settings.setAllowKeyboardEditing(false);
    	DatePicker datepicker = new DatePicker(settings);
    	settings.setVetoPolicy(new DateVetoPolicy() {
            @Override
            public boolean isDateAllowed(LocalDate date) {
                return !date.isBefore(LocalDate.now());
            }
        });
    	datepicker.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(203, 213, 225), 18, 1),
                new EmptyBorder(8, 12, 8, 12)));
    	
		return datepicker;
    	
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void aktuelleZeitSetzen() {
        LocalDateTime jetzt = LocalDateTime.now();
        LocalDateTime spaeter = jetzt.plusMinutes(15);

        DateTimeFormatter datumFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter uhrzeitFormat = DateTimeFormatter.ofPattern("HH:mm");

        txtVonDatum.setDateToToday();
        txtVonDatum.getComponentDateTextField().setEditable(false);
        txtVonUhrzeit.setText(jetzt.format(uhrzeitFormat));
        txtBisDatum.getComponentDateTextField().setEditable(false);
        txtBisUhrzeit.setText(spaeter.format(uhrzeitFormat));

        preisAktualisieren();
    }

    private void parkplaetzeSuchen() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Bezeichnung", "Adresse", "Kapazität", "Stundensatz (€)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String suchOrt = txtSuchOrt.getText().trim().toLowerCase();

        for (Parkplatz p : manager.getAlleParkplaetze()) {
            if (suchOrt.isEmpty() || p.getAdresse().toLowerCase().contains(suchOrt)) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getBezeichnung(),
                        p.getAdresse(),
                        p.getGesamtKapazitaet(),
                        String.format("%.2f", p.getStundenSatz())
                });
            }
        }

        tblParkplaetze.setModel(model);
        applyTableRenderers();

        if (model.getRowCount() == 0) {
            lblPreis.setText("Preis: -");
            JOptionPane.showMessageDialog(this, "Keine passenden Parkplätze gefunden.");
        }
    }

    private void preisAktualisieren() {
        try {
            int zeile = tblParkplaetze.getSelectedRow();

            if (zeile == -1) {
                lblPreis.setText("Preis: -");
                return;
            }

            String parkplatzId = tblParkplaetze.getValueAt(zeile, 0).toString();
            Parkplatz ausgewaehlterParkplatz = findeParkplatzNachId(parkplatzId);

            if (ausgewaehlterParkplatz == null) {
                lblPreis.setText("Preis: -");
                return;
            }

            LocalDateTime von = leseVonZeitpunkt();
            LocalDateTime bis = leseBisZeitpunkt();

            if (!bis.isAfter(von)) {
                lblPreis.setText("Bitte Datum und Uhrzeit überprüfen!");
                return;
            }

            double preis = manager.berechnePreis(ausgewaehlterParkplatz, von, bis);
            lblPreis.setText(String.format("Preis: %.2f €", preis));

        } catch (DateTimeParseException e) {
            lblPreis.setText("Preis: Bitte Datum und Uhrzeit eingeben");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Preisberechnung.");
        }
    }

    private Parkplatz findeParkplatzNachId(String id) {
        for (Parkplatz p : manager.getAlleParkplaetze()) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private LocalDateTime leseVonZeitpunkt() {
       // DateTimeFormatter datumFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter uhrzeitFormat = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate datum = txtVonDatum.getDate();
        LocalTime uhrzeit = LocalTime.parse(txtVonUhrzeit.getText().trim(), uhrzeitFormat);

        return LocalDateTime.of(datum, uhrzeit);
    }

    private LocalDateTime leseBisZeitpunkt() {
     //   DateTimeFormatter datumFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter uhrzeitFormat = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate datum = txtBisDatum.getDate();
        LocalTime uhrzeit = LocalTime.parse(txtBisUhrzeit.getText().trim(), uhrzeitFormat);

        return LocalDateTime.of(datum, uhrzeit);
    }

    private void buchungAusfuehren() {
        try {
            int zeile = tblParkplaetze.getSelectedRow();

            if (zeile == -1) {
                JOptionPane.showMessageDialog(this, "Bitte wähle zuerst einen Parkplatz aus.");
                return;
            }

            if (manager.getAktuellerNutzer() == null) {
                JOptionPane.showMessageDialog(this, "Es ist aktuell kein Nutzer eingeloggt.");
                return;
            }

            if (!(manager.getAktuellerNutzer() instanceof model.Kunde)) {
                JOptionPane.showMessageDialog(this, "Der aktuelle Nutzer ist kein Kunde.");
                return;
            }

            String parkplatzId = tblParkplaetze.getValueAt(zeile, 0).toString();
            Parkplatz ausgewaehlterParkplatz = findeParkplatzNachId(parkplatzId);

            if (ausgewaehlterParkplatz == null) {
                JOptionPane.showMessageDialog(this, "Der ausgewählte Parkplatz wurde nicht gefunden.");
                return;
            }

            LocalDateTime von = leseVonZeitpunkt();
            LocalDateTime bis = leseBisZeitpunkt();

            if (!bis.isAfter(von)) {
                JOptionPane.showMessageDialog(this, "Die Endzeit muss nach der Startzeit liegen.");
                return;
            }

            long minuten = Duration.between(von, bis).toMinutes();
            if (minuten < 15) {
                JOptionPane.showMessageDialog(this, "Die Mindestbuchungsdauer beträgt 15 Minuten.");
                return;
            }

            if (!manager.verfuegbarkeitPruefen(ausgewaehlterParkplatz, von, bis)) {
                JOptionPane.showMessageDialog(this, "Der Parkplatz ist im gewählten Zeitraum nicht verfügbar.");
                return;
            }

            Buchung neueBuchung = manager.bucheParkplatz(ausgewaehlterParkplatz, von, bis);

            if (neueBuchung == null) {
                JOptionPane.showMessageDialog(this, "Buchung konnte nicht durchgeführt werden.");
                return;
            }

            String aufschluesselung = manager.getPreisAufschluesselung(ausgewaehlterParkplatz, von, bis);

            JOptionPane.showMessageDialog(
                    this,
                    "Buchung erfolgreich gespeichert.\n"
                    + "BuchungsCode: " + neueBuchung.getBuchungsCode() + "\n\n"
                    + "Preisübersicht:\n" + aufschluesselung
            );

            preisAktualisieren();
            aktualisiereMeineBuchungen();
            kundenSeitenUmschalter.show(kundenSeitenContainer, "MEINE_BUCHUNGEN");

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bitte gib das Datum im Format dd.MM.yyyy und die Uhrzeit im Format HH:mm ein."
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Buchen.");
        }
    }

    private static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(3, 3, 3, 3);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 3;
            insets.right = 3;
            insets.top = 3;
            insets.bottom = 3;
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(
                    x + 0.5,
                    y + 0.5,
                    width - 1.0,
                    height - 1.0,
                    radius,
                    radius
            ));
            g2.dispose();
        }
    }

    private static class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(10, 18, 10, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color start = getModel().isPressed() ? PRIMARY_DARK : PRIMARY;
            Color end = getModel().isRollover() ? ACCENT : PRIMARY_DARK;

            g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(99, 102, 241),
                    getWidth(), getHeight(), new Color(59, 130, 246)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);
            g2.dispose();
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }
}