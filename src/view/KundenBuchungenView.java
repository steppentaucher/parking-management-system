package view;

import controller.PlattformManager;
import model.Buchung;
import model.Kunde;
import model.Parkplatz;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;

public class KundenBuchungenView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color PAGE_BG = new Color(245, 247, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TABLE_HEADER_BG = new Color(239, 243, 255);
    private static final Color TABLE_ROW_ALT = new Color(249, 250, 255);

    private final PlattformManager manager;
    public final MainFrame mainFrame;

    private JTable tblBuchungen;
    private JButton btnAktualisieren;
    private JButton btnZurueck;
    private JLabel lblInfo;

    public KundenBuchungenView(PlattformManager manager, MainFrame mainFrame) {
        this.manager = manager;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(14, 14, 14, 14));
        setBackground(PAGE_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        buchungenLaden();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new GradientPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 24, 20, 24));
        header.setPreferredSize(new Dimension(0, 110));

        JLabel lblTitel = new JLabel("Meine Buchungen");
        lblTitel.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitel.setForeground(Color.WHITE);

        JLabel lblUntertitel = new JLabel("Hier siehst du alle gebuchten Parkplätze des aktuell eingeloggten Kunden.");
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

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);

        JPanel card = createCardPanel("Gebuchte Parkplätze");
        card.setLayout(new BorderLayout(0, 10));

        lblInfo = new JLabel("Lade Buchungen ...");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblInfo.setForeground(TEXT_MUTED);

        tblBuchungen = new JTable();
        tblBuchungen.setModel(new DefaultTableModel(
                new Object[]{"Buchungscode", "Parkplatz", "Adresse", "Von", "Bis"}, 0
        ));
        tblBuchungen.setRowHeight(32);
        tblBuchungen.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblBuchungen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBuchungen.setGridColor(new Color(235, 238, 245));
        tblBuchungen.setShowVerticalLines(false);
        tblBuchungen.setShowHorizontalLines(true);
        tblBuchungen.setIntercellSpacing(new Dimension(0, 1));
        tblBuchungen.setSelectionBackground(new Color(224, 231, 255));
        tblBuchungen.setSelectionForeground(TEXT_DARK);
        tblBuchungen.setFillsViewportHeight(true);

        tblBuchungen.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tblBuchungen.getTableHeader().setBackground(TABLE_HEADER_BG);
        tblBuchungen.getTableHeader().setForeground(TEXT_DARK);
        tblBuchungen.getTableHeader().setReorderingAllowed(false);

        applyTableRenderers();

        JScrollPane scrollPane = new JScrollPane(tblBuchungen);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        btnAktualisieren = new GradientButton("Aktualisieren");
        btnAktualisieren.setPreferredSize(new Dimension(170, 46));
        btnAktualisieren.addActionListener(e -> buchungenLaden());

        btnZurueck = new GradientButton("Zurück zum Buchen");
        btnZurueck.setPreferredSize(new Dimension(220, 46));
        btnZurueck.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.zeigeKundenView();
            }
        });

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(btnZurueck);
        rightButtons.add(btnAktualisieren);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblInfo, BorderLayout.WEST);
        top.add(rightButtons, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private void buchungenLaden() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Buchungscode", "Parkplatz", "Adresse", "Von", "Bis"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (manager.getAktuellerNutzer() == null || !(manager.getAktuellerNutzer() instanceof Kunde)) {
            tblBuchungen.setModel(model);
            applyTableRenderers();
            lblInfo.setText("Es ist aktuell kein Kunde eingeloggt.");
            return;
        }

        Kunde kunde = (Kunde) manager.getAktuellerNutzer();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        int anzahl = 0;

        for (Buchung buchung : manager.getAlleBuchungen()) {
            if (buchung.getKunde() != null && buchung.getKunde().getId().equals(kunde.getId())) {
                Parkplatz parkplatz = buchung.getParkplatz();

                model.addRow(new Object[]{
                        buchung.getBuchungsCode(),
                        parkplatz != null ? parkplatz.getBezeichnung() : "-",
                        parkplatz != null ? parkplatz.getAdresse() : "-",
                        buchung.getVon().format(formatter),
                        buchung.getBis().format(formatter)
                });

                anzahl++;
            }
        }

        tblBuchungen.setModel(model);
        applyTableRenderers();

        if (anzahl == 0) {
            lblInfo.setText("Für diesen Kunden wurden noch keine Buchungen gefunden.");
        } else {
            lblInfo.setText("Gefundene Buchungen: " + anzahl);
        }
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

        if (tblBuchungen.getColumnModel().getColumnCount() >= 5) {
            tblBuchungen.getColumnModel().getColumn(0).setCellRenderer(centeredRenderer);
            tblBuchungen.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
            tblBuchungen.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
            tblBuchungen.getColumnModel().getColumn(3).setCellRenderer(centeredRenderer);
            tblBuchungen.getColumnModel().getColumn(4).setCellRenderer(centeredRenderer);
        }
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(createRoundedTitledBorder(title));
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
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(10, 18, 10, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color start = getModel().isPressed() ? PRIMARY.darker() : PRIMARY;
            Color end = getModel().isRollover() ? ACCENT : PRIMARY.darker();

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