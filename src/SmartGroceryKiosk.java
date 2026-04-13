import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SmartGroceryKiosk extends JFrame {

    private static final long serialVersionUID = 1L;

    // ── Color Palette (matching the PDF's olive/sage green theme) ──
    static final Color PRIMARY_GREEN = new Color(107, 114, 81);
    static final Color LIGHT_GREEN = new Color(139, 148, 108);
    static final Color PALE_GREEN = new Color(190, 197, 170);
    static final Color BG_GRAY = new Color(230, 230, 230);
    static final Color CARD_WHITE = new Color(255, 255, 255);
    static final Color TEXT_DARK = new Color(40, 40, 40);
    static final Color TEXT_MEDIUM = new Color(110, 110, 110);
    static final Color ICON_GREEN = new Color(107, 114, 81);
    static final Color HOVER_GREEN = new Color(125, 133, 98);

    // ── Data ──
    static final String[] CATEGORIES = {"Fruits", "Vegetables", "Tools", "Snacks", "Dairy", "Meat"};
    static final String[] CAT_EMOJI = {"\uD83C\uDF4E", "\uD83E\uDD66", "\uD83D\uDD27", "\uD83C\uDF7F", "\uD83E\uDDC8", "\uD83E\uDD69"};
    static Map<String, List<Product>> productsByCategory = new LinkedHashMap<>();
    static List<CartItem> cart = new ArrayList<>();

    // ── Panels ──
    CardLayout cardLayout;
    JPanel mainPanel;

    public static void main(String[] args) {
        initProducts();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new SmartGroceryKiosk();
        });
    }

    static void initProducts() {
        productsByCategory.put("Fruits", Arrays.asList(
            new Product("Apple", "Fresh red apple, 1kg", 95.00, "Aisle 2", "Fruits"),
            new Product("Banana", "Lakatan banana, bundle", 65.00, "Aisle 2", "Fruits"),
            new Product("Mango", "Sweet Philippine mango", 120.00, "Aisle 2", "Fruits"),
            new Product("Grapes", "Seedless green grapes, 500g", 180.00, "Aisle 2", "Fruits"),
            new Product("Orange", "Valencia orange, 1kg", 110.00, "Aisle 2", "Fruits")
        ));
        productsByCategory.put("Vegetables", Arrays.asList(
            new Product("Tomato", "Fresh tomatoes, 1kg", 80.00, "Aisle 3", "Vegetables"),
            new Product("Cabbage", "Green cabbage, whole", 55.00, "Aisle 3", "Vegetables"),
            new Product("Carrot", "Fresh carrots, 500g", 60.00, "Aisle 3", "Vegetables"),
            new Product("Onion", "Red onion, 1kg", 90.00, "Aisle 3", "Vegetables"),
            new Product("Potato", "Washed potatoes, 1kg", 75.00, "Aisle 3", "Vegetables")
        ));
        productsByCategory.put("Tools", Arrays.asList(
            new Product("Knife Set", "3-piece kitchen knife set", 350.00, "Aisle 6", "Tools"),
            new Product("Cutting Board", "Bamboo cutting board", 220.00, "Aisle 6", "Tools"),
            new Product("Peeler", "Stainless steel peeler", 85.00, "Aisle 6", "Tools"),
            new Product("Tongs", "Silicone-tip kitchen tongs", 120.00, "Aisle 6", "Tools")
        ));
        productsByCategory.put("Snacks", Arrays.asList(
            new Product("Chips", "Potato chips, 150g bag", 65.00, "Aisle 4", "Snacks"),
            new Product("Crackers", "Cream crackers, 200g", 55.00, "Aisle 4", "Snacks"),
            new Product("Cookies", "Chocolate chip cookies", 90.00, "Aisle 4", "Snacks"),
            new Product("Nuts", "Mixed nuts, 250g", 150.00, "Aisle 4", "Snacks"),
            new Product("Popcorn", "Microwave popcorn, 3-pack", 110.00, "Aisle 4", "Snacks")
        ));
        productsByCategory.put("Dairy", Arrays.asList(
            new Product("Magnolia", "Fresh milk, 1L", 85.00, "Aisle 1", "Dairy"),
            new Product("Nestle", "Full cream milk, 1L", 92.00, "Aisle 1", "Dairy"),
            new Product("Oatside", "Oat milk, barista blend 1L", 165.00, "Aisle 1", "Dairy"),
            new Product("Cheese", "Cheddar cheese block, 250g", 140.00, "Aisle 1", "Dairy"),
            new Product("Yogurt", "Greek yogurt, 500g", 125.00, "Aisle 1", "Dairy")
        ));
        productsByCategory.put("Meat", Arrays.asList(
            new Product("Chicken", "Whole chicken, 1.2kg", 220.00, "Aisle 5", "Meat"),
            new Product("Pork Chop", "Pork chop, 500g", 185.00, "Aisle 5", "Meat"),
            new Product("Ground Beef", "Lean ground beef, 500g", 250.00, "Aisle 5", "Meat"),
            new Product("Hotdog", "Jumbo hotdog, 500g", 130.00, "Aisle 5", "Meat"),
            new Product("Bacon", "Smoked bacon, 250g", 175.00, "Aisle 5", "Meat")
        ));
    }

    public SmartGroceryKiosk() {
        setTitle("JAVA.R Smart Grocery Kiosk");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 780);
        setMinimumSize(new Dimension(500, 720));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_GRAY);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_GRAY);

        mainPanel.add(createWelcomePanel(), "welcome");
        mainPanel.add(createCategoriesPanel(), "categories");
        mainPanel.add(createSearchPanel(), "search");
        mainPanel.add(createPromotionsPanel(), "promotions");

        add(mainPanel);
        setVisible(true);
    }

    JPanel wrapInOuter(RoundedPanel card) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ═══════════════════════════════════════════════════════════════
    //  WELCOME SCREEN
    // ═══════════════════════════════════════════════════════════════
    JPanel createWelcomePanel() {
        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(18, 25, 0, 18);
        card.add(createTopBar(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(30, 40, 0, 40);
        JLabel welcomeLabel = new JLabel("Welcome to");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 38));
        welcomeLabel.setForeground(TEXT_DARK);
        card.add(welcomeLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 45, 0, 40);
        JLabel brandLabel = new JLabel("JAVA.R");
        brandLabel.setFont(new Font("Serif", Font.PLAIN, 48));
        brandLabel.setForeground(TEXT_DARK);
        card.add(brandLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(-2, 47, 0, 40);
        JLabel subLabel = new JLabel("S M A R T   G R O C E R Y");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subLabel.setForeground(TEXT_MEDIUM);
        card.add(subLabel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        String[] labels = {"BROWSE CATEGORIES", "SEARCH PRODUCTS", "PROMOTIONS"};
        String[] targets = {"categories", "search", "promotions"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = 4 + i;
            gbc.insets = new Insets(i == 0 ? 40 : 12, 40, 0, 40);
            RoundedButton btn = new RoundedButton(labels[i]);
            btn.setPreferredSize(new Dimension(280, 48));
            final String target = targets[i];
            btn.addActionListener(e -> showScreen(target));
            card.add(btn, gbc);
        }

        gbc.gridy = 7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        card.add(Box.createGlue(), gbc);

        gbc.gridy = 8;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 25, 0);
        JPanel bottomIcons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        bottomIcons.setOpaque(false);
        // Drawn cart icon button
        JButton cartBtn = new JButton() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.8f));
                // Basket body
                g2.drawRoundRect(cx - 10, cy - 4, 20, 12, 3, 3);
                // Handle
                g2.drawArc(cx - 6, cy - 12, 12, 12, 0, 180);
                g2.dispose();
            }
        };
        cartBtn.setPreferredSize(new Dimension(32, 32));
        cartBtn.setBorderPainted(false);
        cartBtn.setContentAreaFilled(false);
        cartBtn.setFocusPainted(false);
        cartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartBtn.addActionListener(e -> showCartScreen());
        bottomIcons.add(cartBtn);
        // Drawn location pin
        JLabel locIcon = new JLabel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.8f));
                // Pin body
                g2.drawOval(cx - 6, cy - 10, 12, 12);
                // Pin point
                g2.drawLine(cx - 4, cy, cx, cy + 10);
                g2.drawLine(cx + 4, cy, cx, cy + 10);
                // Inner dot
                g2.fillOval(cx - 2, cy - 6, 4, 4);
                g2.dispose();
            }
        };
        locIcon.setPreferredSize(new Dimension(32, 32));
        bottomIcons.add(locIcon);
        card.add(bottomIcons, gbc);

        return wrapInOuter(card);
    }

    // ═══════════════════════════════════════════════════════════════
    //  BROWSE CATEGORIES
    // ═══════════════════════════════════════════════════════════════
    JPanel createCategoriesPanel() {
        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(18, 25, 0, 18);
        card.add(createTopBar(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(25, 20, 10, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel title = new JLabel("BROWSE CATEGORIES");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        card.add(title, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(15, 30, 0, 30);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JPanel grid = new JPanel(new GridLayout(2, 3, 25, 25));
        grid.setOpaque(false);
        for (int i = 0; i < CATEGORIES.length; i++) {
            grid.add(createCategoryCard(CATEGORIES[i], CAT_EMOJI[i]));
        }
        card.add(grid, gbc);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 25, 18, 0);
        card.add(makeBackButton("welcome"), gbc);

        return wrapInOuter(card);
    }

    JPanel createCategoryCard(String name, String emoji) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;

        g.gridy = 0;
        g.insets = new Insets(5, 5, 5, 5);
        JPanel circle = new JPanel(new GridBagLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                Graphics2D g2 = (Graphics2D) gr;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PALE_GREEN);
                g2.setStroke(new BasicStroke(2f));
                int d = Math.min(getWidth(), getHeight()) - 6;
                g2.drawOval((getWidth() - d) / 2, (getHeight() - d) / 2, d, d);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(90, 90));
        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 36));
        circle.add(icon);
        panel.add(circle, g);

        g.gridy = 1;
        g.insets = new Insets(2, 0, 0, 0);
        JLabel label = new JLabel(name);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXT_DARK);
        panel.add(label, g);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { showProductList(name); }
        });
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════
    //  SEARCH PRODUCTS
    // ═══════════════════════════════════════════════════════════════
    JPanel createSearchPanel() {
        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(18, 25, 0, 18);
        card.add(createTopBar(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(18, 20, 10, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel title = new JLabel("SEARCH PRODUCTS");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        card.add(title, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 35, 10, 35);
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PALE_GREEN, 2, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        RoundedButton searchBtn = new RoundedButton("Search");
        searchBtn.setPreferredSize(new Dimension(90, 38));
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchBtn, BorderLayout.EAST);
        card.add(searchRow, gbc);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 25, 5, 25);
        card.add(scroll, gbc);

        ActionListener searchAction = e -> {
            String query = searchField.getText().trim().toLowerCase();
            resultsPanel.removeAll();
            if (!query.isEmpty()) {
                boolean found = false;
                for (Map.Entry<String, List<Product>> entry : productsByCategory.entrySet()) {
                    for (Product p : entry.getValue()) {
                        if (p.name.toLowerCase().contains(query) || p.description.toLowerCase().contains(query)) {
                            resultsPanel.add(createProductRow(p));
                            resultsPanel.add(Box.createVerticalStrut(6));
                            found = true;
                        }
                    }
                }
                if (!found) {
                    JLabel nr = new JLabel("No products found.");
                    nr.setFont(new Font("SansSerif", Font.ITALIC, 14));
                    nr.setForeground(TEXT_MEDIUM);
                    nr.setAlignmentX(Component.CENTER_ALIGNMENT);
                    resultsPanel.add(Box.createVerticalStrut(30));
                    resultsPanel.add(nr);
                }
            }
            resultsPanel.revalidate();
            resultsPanel.repaint();
        };
        searchBtn.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 25, 18, 0);
        card.add(makeBackButton("welcome"), gbc);

        return wrapInOuter(card);
    }

    // ═══════════════════════════════════════════════════════════════
    //  PROMOTIONS
    // ═══════════════════════════════════════════════════════════════
    JPanel createPromotionsPanel() {
        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(18, 25, 0, 18);
        card.add(createTopBar(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 20, 15, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel title = new JLabel("PROMOTIONS");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        card.add(title, gbc);

        String[][] promos = {
            {"Buy 1 Get 1 Free!", "All Magnolia Fresh Milk 1L", "Until April 30, 2026"},
            {"20% OFF", "Selected fruits and vegetables", "Weekends only"},
            {"\u20B120 Discount", "On orders above \u20B1200", "Use code: SMART20"}
        };

        for (int i = 0; i < promos.length; i++) {
            gbc.gridy = 2 + i;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 30, 5, 30);
            JPanel promoCard = new JPanel();
            promoCard.setLayout(new BoxLayout(promoCard, BoxLayout.Y_AXIS));
            promoCard.setBackground(new Color(245, 247, 240));
            promoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PALE_GREEN, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
            JLabel pTitle = new JLabel(promos[i][0]);
            pTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
            pTitle.setForeground(PRIMARY_GREEN);
            JLabel pDesc = new JLabel(promos[i][1]);
            pDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
            pDesc.setForeground(TEXT_DARK);
            JLabel pDate = new JLabel(promos[i][2]);
            pDate.setFont(new Font("SansSerif", Font.ITALIC, 11));
            pDate.setForeground(TEXT_MEDIUM);
            promoCard.add(pTitle);
            promoCard.add(Box.createVerticalStrut(3));
            promoCard.add(pDesc);
            promoCard.add(Box.createVerticalStrut(2));
            promoCard.add(pDate);
            card.add(promoCard, gbc);
        }

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        card.add(Box.createGlue(), gbc);

        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 25, 18, 0);
        card.add(makeBackButton("welcome"), gbc);

        return wrapInOuter(card);
    }

    // ═══════════════════════════════════════════════════════════════
    //  PRODUCT LIST
    // ═══════════════════════════════════════════════════════════════
    void showProductList(String category) {
        String panelName = "products_" + category;
        for (Component c : mainPanel.getComponents()) {
            if (panelName.equals(c.getName())) { mainPanel.remove(c); break; }
        }

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(18, 25, 0, 18);
        JPanel topBar = createTopBar();
        String aisle = productsByCategory.get(category).get(0).aisle;
        JLabel aisleLabel = new JLabel(aisle, SwingConstants.CENTER);
        aisleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        aisleLabel.setForeground(TEXT_MEDIUM);
        topBar.add(aisleLabel, BorderLayout.CENTER);
        card.add(topBar, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 25, 0, 25);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(makeBackButton("categories"), gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 5, 0);
        int catIdx = Arrays.asList(CATEGORIES).indexOf(category);
        JPanel catHeader = new JPanel();
        catHeader.setOpaque(false);
        catHeader.setLayout(new BoxLayout(catHeader, BoxLayout.Y_AXIS));
        JLabel catIcon = new JLabel(CAT_EMOJI[catIdx], SwingConstants.CENTER);
        catIcon.setFont(new Font("SansSerif", Font.PLAIN, 34));
        catIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel catName = new JLabel(category, SwingConstants.CENTER);
        catName.setFont(new Font("Serif", Font.PLAIN, 24));
        catName.setForeground(TEXT_DARK);
        catName.setAlignmentX(Component.CENTER_ALIGNMENT);
        catHeader.add(catIcon);
        catHeader.add(catName);
        card.add(catHeader, gbc);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        for (Product p : productsByCategory.get(category)) {
            listPanel.add(createProductRow(p));
            listPanel.add(Box.createVerticalStrut(4));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(8, 20, 18, 20);
        card.add(scroll, gbc);

        JPanel outer = wrapInOuter(card);
        outer.setName(panelName);
        mainPanel.add(outer, panelName);
        cardLayout.show(mainPanel, panelName);
    }

    JPanel createProductRow(Product p) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 8, 10, 8)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        JPanel iconBox = createCategoryIconBox(p.category, 58, 58);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel nameL = new JLabel(p.name);
        nameL.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameL.setForeground(TEXT_DARK);
        JLabel descL = new JLabel(p.description);
        descL.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descL.setForeground(TEXT_MEDIUM);
        JLabel aisleL = new JLabel(p.aisle);
        aisleL.setFont(new Font("SansSerif", Font.ITALIC, 10));
        aisleL.setForeground(LIGHT_GREEN);
        info.add(nameL);
        info.add(Box.createVerticalStrut(2));
        info.add(descL);
        info.add(aisleL);

        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
        rightSide.setOpaque(false);
        JLabel priceL = new JLabel(String.format("\u20B1%.2f", p.price));
        priceL.setFont(new Font("SansSerif", Font.BOLD, 13));
        priceL.setForeground(TEXT_DARK);
        priceL.setAlignmentX(Component.RIGHT_ALIGNMENT);
        RoundedButton addBtn = new RoundedButton("Add to cart");
        addBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        addBtn.setPreferredSize(new Dimension(95, 28));
        addBtn.setMaximumSize(new Dimension(95, 28));
        addBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        addBtn.addActionListener(e -> {
            addToCart(p);
            JOptionPane.showMessageDialog(this, p.name + " added to cart!", "Added", JOptionPane.INFORMATION_MESSAGE);
        });
        rightSide.add(priceL);
        rightSide.add(Box.createVerticalStrut(6));
        rightSide.add(addBtn);

        row.add(iconBox, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        row.add(rightSide, BorderLayout.EAST);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════
    //  SHOPPING CART
    // ═══════════════════════════════════════════════════════════════
    void showCartScreen() {
        String panelName = "cart_screen";
        for (Component c : mainPanel.getComponents()) {
            if (panelName.equals(c.getName())) { mainPanel.remove(c); break; }
        }

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(18, 25, 0, 18);
        card.add(createTopBar(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 25, 0, 25);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(makeBackButton("welcome"), gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 8, 0);
        JPanel cartHeader = new JPanel();
        cartHeader.setOpaque(false);
        cartHeader.setLayout(new BoxLayout(cartHeader, BoxLayout.Y_AXIS));
        JPanel cartIconPanel = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(2.5f));
                // Cart body
                int[] bx = {cx - 14, cx + 14, cx + 10, cx - 10};
                int[] by = {cy - 6, cy - 6, cy + 8, cy + 8};
                g2.drawPolygon(bx, by, 4);
                // Handle
                g2.drawLine(cx - 14, cy - 6, cx - 18, cy - 14);
                // Wheels
                g2.fillOval(cx - 8, cy + 10, 6, 6);
                g2.fillOval(cx + 4, cy + 10, 6, 6);
                g2.dispose();
            }
        };
        cartIconPanel.setOpaque(false);
        cartIconPanel.setPreferredSize(new Dimension(50, 40));
        cartIconPanel.setMaximumSize(new Dimension(50, 40));
        cartIconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel cartTitle = new JLabel("Your Shopping Cart");
        cartTitle.setFont(new Font("Serif", Font.PLAIN, 22));
        cartTitle.setForeground(TEXT_DARK);
        cartTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartHeader.add(cartIconPanel);
        cartHeader.add(cartTitle);
        card.add(cartHeader, gbc);

        if (cart.isEmpty()) {
            gbc.gridy = 3;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(30, 0, 0, 0);
            JLabel empty = new JLabel("Your cart is empty.", SwingConstants.CENTER);
            empty.setFont(new Font("SansSerif", Font.ITALIC, 14));
            empty.setForeground(TEXT_MEDIUM);
            card.add(empty, gbc);
        } else {
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setOpaque(false);
            for (CartItem item : cart) {
                listPanel.add(createCartRow(item));
                listPanel.add(Box.createVerticalStrut(4));
            }
            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);

            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(5, 20, 5, 20);
            card.add(scroll, gbc);

            double subtotal = 0;
            for (CartItem item : cart) subtotal += item.product.price * item.qty;
            double discount = subtotal >= 200 ? 20.0 : 0.0;
            double total = subtotal - discount;

            gbc.gridy = 4;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 25, 5, 25);
            JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
            bottomBar.setOpaque(false);
            JPanel subtotalInfo = new JPanel();
            subtotalInfo.setLayout(new BoxLayout(subtotalInfo, BoxLayout.Y_AXIS));
            subtotalInfo.setOpaque(false);
            RoundedButton subtotalBtn = new RoundedButton(String.format("Subtotal: \u20B1%.2f", subtotal));
            subtotalBtn.setPreferredSize(new Dimension(170, 32));
            subtotalBtn.setEnabled(false);
            subtotalBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            if (discount > 0) {
                JLabel discLabel = new JLabel(String.format("Discount: -\u20B1%.2f", discount));
                discLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                discLabel.setForeground(PRIMARY_GREEN);
                subtotalInfo.add(discLabel);
            }
            subtotalInfo.add(subtotalBtn);
            bottomBar.add(subtotalInfo, BorderLayout.WEST);
            RoundedButton removeBtn = new RoundedButton("Remove");
            removeBtn.setPreferredSize(new Dimension(100, 32));
            removeBtn.addActionListener(e -> { cart.clear(); showCartScreen(); });
            bottomBar.add(removeBtn, BorderLayout.EAST);
            card.add(bottomBar, gbc);

            gbc.gridy = 5;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(5, 0, 18, 0);
            double finalTotal = total;
            RoundedButton checkoutBtn = new RoundedButton(String.format("Proceed to checkout: \u20B1%.2f", total));
            checkoutBtn.setPreferredSize(new Dimension(260, 42));
            checkoutBtn.addActionListener(e -> showPaymentScreen(finalTotal));
            card.add(checkoutBtn, gbc);
        }

        JPanel outer = wrapInOuter(card);
        outer.setName(panelName);
        mainPanel.add(outer, panelName);
        cardLayout.show(mainPanel, panelName);
    }

    JPanel createCartRow(CartItem item) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 8, 10, 8)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        JPanel iconBox = createCategoryIconBox(item.product.category, 55, 55);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel name = new JLabel(item.product.name);
        name.setFont(new Font("SansSerif", Font.BOLD, 14));
        name.setForeground(TEXT_DARK);
        JLabel desc = new JLabel(item.product.description);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 11));
        desc.setForeground(TEXT_MEDIUM);
        info.add(name);
        info.add(desc);

        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
        rightSide.setOpaque(false);
        JLabel price = new JLabel(String.format("\u20B1%.2f", item.product.price * item.qty));
        price.setFont(new Font("SansSerif", Font.BOLD, 13));
        price.setForeground(TEXT_DARK);
        price.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        qtyPanel.setOpaque(false);
        RoundedButton minusBtn = new RoundedButton("") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawLine(cx - 6, cy, cx + 6, cy);
                g2.dispose();
            }
        };
        minusBtn.setPreferredSize(new Dimension(36, 30));
        JLabel qtyLabel = new JLabel("  " + item.qty + "  ", SwingConstants.CENTER);
        qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        RoundedButton plusBtn = new RoundedButton("") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawLine(cx - 6, cy, cx + 6, cy);
                g2.drawLine(cx, cy - 6, cx, cy + 6);
                g2.dispose();
            }
        };
        plusBtn.setPreferredSize(new Dimension(36, 30));

        minusBtn.addActionListener(e -> {
            if (item.qty > 1) item.qty--;
            else cart.remove(item);
            showCartScreen();
        });
        plusBtn.addActionListener(e -> { item.qty++; showCartScreen(); });

        qtyPanel.add(minusBtn);
        qtyPanel.add(qtyLabel);
        qtyPanel.add(plusBtn);
        qtyPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightSide.add(price);
        rightSide.add(Box.createVerticalStrut(5));
        rightSide.add(qtyPanel);

        row.add(iconBox, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        row.add(rightSide, BorderLayout.EAST);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════
    //  PAYMENT
    // ═══════════════════════════════════════════════════════════════
    void showPaymentScreen(double total) {
        String panelName = "payment";
        for (Component c : mainPanel.getComponents()) {
            if (panelName.equals(c.getName())) { mainPanel.remove(c); break; }
        }

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(18, 25, 0, 18);
        card.add(createTopBar(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 25, 0, 25);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(makeBackButton("cart_screen_back"), gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 0, 0);
        JPanel moneyPanel = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(2f));
                // Bill shape (back)
                g2.drawRoundRect(cx - 28, cy - 12, 48, 30, 4, 4);
                // Bill shape (front)
                g2.setColor(new Color(245, 247, 240));
                g2.fillRoundRect(cx - 22, cy - 16, 48, 30, 4, 4);
                g2.setColor(PRIMARY_GREEN);
                g2.drawRoundRect(cx - 22, cy - 16, 48, 30, 4, 4);
                // Dollar circle
                g2.drawOval(cx - 6, cy - 10, 16, 16);
                // $ sign
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString("$", cx - 1, cy + 3);
                g2.dispose();
            }
        };
        moneyPanel.setOpaque(false);
        moneyPanel.setPreferredSize(new Dimension(80, 50));
        card.add(moneyPanel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 0, 0);
        JLabel payTitle = new JLabel("COMPLETE PAYMENT");
        payTitle.setFont(new Font("Serif", Font.BOLD, 22));
        payTitle.setForeground(TEXT_DARK);
        card.add(payTitle, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 10, 0);
        JLabel totalLabel = new JLabel(String.format("Total: \u20B1%.2f", total));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(PRIMARY_GREEN);
        card.add(totalLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 15, 0);
        JPanel qrPanel = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawQRCode((Graphics2D) g, getWidth(), getHeight());
            }
        };
        qrPanel.setOpaque(false);
        qrPanel.setPreferredSize(new Dimension(200, 200));
        card.add(qrPanel, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 0, 0);
        RoundedButton receiptBtn = new RoundedButton("Generate Receipt");
        receiptBtn.setPreferredSize(new Dimension(220, 42));
        receiptBtn.addActionListener(e -> showReceiptScreen(total));
        card.add(receiptBtn, gbc);

        gbc.gridy = 7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        card.add(Box.createGlue(), gbc);

        JPanel outer = wrapInOuter(card);
        outer.setName(panelName);
        mainPanel.add(outer, panelName);
        cardLayout.show(mainPanel, panelName);
    }

    void drawQRCode(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cellSize = 9;
        int gridSize = 21;
        int totalSize = gridSize * cellSize;
        int offsetX = (w - totalSize) / 2;
        int offsetY = (h - totalSize) / 2;
        Random rand = new Random(42);
        g2.setColor(Color.BLACK);
        drawFinderPattern(g2, offsetX, offsetY, cellSize);
        drawFinderPattern(g2, offsetX + 14 * cellSize, offsetY, cellSize);
        drawFinderPattern(g2, offsetX, offsetY + 14 * cellSize, cellSize);
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (isInFinderArea(row, col)) continue;
                if (rand.nextBoolean())
                    g2.fillRect(offsetX + col * cellSize, offsetY + row * cellSize, cellSize, cellSize);
            }
        }
    }

    void drawFinderPattern(Graphics2D g2, int x, int y, int cell) {
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, 7 * cell, 7 * cell);
        g2.setColor(Color.WHITE);
        g2.fillRect(x + cell, y + cell, 5 * cell, 5 * cell);
        g2.setColor(Color.BLACK);
        g2.fillRect(x + 2 * cell, y + 2 * cell, 3 * cell, 3 * cell);
    }

    boolean isInFinderArea(int row, int col) {
        return (row < 8 && col < 8) || (row < 8 && col > 12) || (row > 12 && col < 8);
    }

    // ═══════════════════════════════════════════════════════════════
    //  RECEIPT
    // ═══════════════════════════════════════════════════════════════
    void showReceiptScreen(double total) {
        String panelName = "receipt";
        for (Component c : mainPanel.getComponents()) {
            if (panelName.equals(c.getName())) { mainPanel.remove(c); break; }
        }

        // Use a regular JPanel for the inner content to avoid rendering issues
        JPanel card = new JPanel();
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 35, 25, 35));
        card.setOpaque(true);

        JPanel dotsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dotsRow.setOpaque(false);
        dotsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        dotsRow.add(createWindowDots());
        card.add(dotsRow);
        card.add(Box.createVerticalStrut(15));

        JLabel brand = new JLabel("JAVA.R", SwingConstants.CENTER);
        brand.setFont(new Font("Serif", Font.ITALIC, 38));
        brand.setForeground(TEXT_DARK);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(brand);
        JLabel smartLabel = new JLabel("S M A R T   G R O C E R Y", SwingConstants.CENTER);
        smartLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        smartLabel.setForeground(TEXT_MEDIUM);
        smartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(smartLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(createDashedLine());
        card.add(Box.createVerticalStrut(8));

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat stf = new SimpleDateFormat("h:mm a");
        Date now = new Date();
        String transId = String.format("%08d", new Random().nextInt(99999999));
        addReceiptLine(card, "Date: " + sdf.format(now) + "  Time: " + stf.format(now));
        addReceiptLine(card, "Transaction ID: " + transId);
        card.add(Box.createVerticalStrut(8));
        JLabel itemsHeader = new JLabel("Items Purchased:");
        itemsHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        itemsHeader.setForeground(TEXT_DARK);
        itemsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(itemsHeader);
        card.add(createDashedLine());
        card.add(Box.createVerticalStrut(5));

        double subtotal = 0;
        for (CartItem item : cart) {
            double lineTotal = item.product.price * item.qty;
            subtotal += lineTotal;
            addReceiptLine(card, String.format("%-18s x%d   \u20B1%.2f", item.product.name, item.qty, lineTotal));
        }
        card.add(Box.createVerticalStrut(5));
        double discount = subtotal >= 200 ? 20.0 : 0.0;
        addReceiptLine(card, String.format("Subtotal:            \u20B1%.2f", subtotal));
        if (discount > 0) addReceiptLine(card, String.format("Discount:           -\u20B1%.2f", discount));
        card.add(createDashedLine());
        JLabel totalLine = new JLabel(String.format("TOTAL:               \u20B1%.2f", total));
        totalLine.setFont(new Font("Monospaced", Font.BOLD, 13));
        totalLine.setForeground(TEXT_DARK);
        totalLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totalLine);
        card.add(Box.createVerticalStrut(5));
        card.add(createDashedLine());
        card.add(Box.createVerticalStrut(5));
        addReceiptLine(card, "Payment Method:  QR Code");
        addReceiptLine(card, "Status:  PAID");
        card.add(Box.createVerticalStrut(10));
        JLabel thanks = new JLabel("Thank you for shopping!");
        thanks.setFont(new Font("Monospaced", Font.ITALIC, 12));
        thanks.setForeground(TEXT_DARK);
        thanks.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(thanks);
        card.add(createDashedLine());
        card.add(Box.createVerticalStrut(15));

        RoundedButton doneBtn = new RoundedButton("Done");
        doneBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneBtn.setMaximumSize(new Dimension(150, 40));
        doneBtn.setPreferredSize(new Dimension(150, 40));
        doneBtn.addActionListener(e -> { cart.clear(); showScreen("welcome"); });
        card.add(doneBtn);
        card.add(Box.createVerticalStrut(15));

        // No scroll needed - receipt is compact. Center it in the window.
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setName(panelName);
        outer.setBackground(BG_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        // Wrap card in RoundedPanel for rounded look
        RoundedPanel wrapper = new RoundedPanel(25);
        wrapper.setBackground(CARD_WHITE);
        wrapper.setLayout(new BorderLayout());
        wrapper.add(card, BorderLayout.CENTER);

        outer.add(wrapper, gbc);
        mainPanel.add(outer, panelName);
        cardLayout.show(mainPanel, panelName);

        outer.revalidate();
        outer.repaint();
    }

    void addReceiptLine(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Monospaced", Font.PLAIN, 11));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
    }

    JComponent createDashedLine() {
        JPanel line = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(TEXT_MEDIUM);
                float[] dash = {4, 3};
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dash, 0));
                g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        };
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(Integer.MAX_VALUE, 8));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        return line;
    }

    // ═══════════════════════════════════════════════════════════════
    //  SHARED UI
    // ═══════════════════════════════════════════════════════════════

    JPanel createCategoryIconBox(String category, int w, int h) {
        JPanel iconBox = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int pw = getWidth(), ph = getHeight();
                // Background
                g2.setColor(new Color(245, 247, 240));
                g2.fillRoundRect(0, 0, pw, ph, 12, 12);
                g2.setColor(PALE_GREEN);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, pw - 1, ph - 1, 12, 12);
                // Draw icon centered
                drawCategoryIcon(g2, category, pw, ph);
                g2.dispose();
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(w, h));
        return iconBox;
    }

    void drawCategoryIcon(Graphics2D g2, String category, int w, int h) {
        int cx = w / 2, cy = h / 2;
        g2.setStroke(new BasicStroke(1.5f));
        switch (category) {
            case "Dairy":
                // Milk carton with cow face
                int bx = cx - 12, by = cy - 14, bw = 24, bh = 30;
                // Carton body
                g2.setColor(Color.WHITE);
                g2.fillRect(bx, by + 8, bw, bh - 8);
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRect(bx, by + 8, bw, bh - 8);
                // Carton top (triangle roof)
                int[] tx = {bx, cx, bx + bw};
                int[] ty = {by + 8, by, by + 8};
                g2.setColor(Color.WHITE);
                g2.fillPolygon(tx, ty, 3);
                g2.setColor(PRIMARY_GREEN);
                g2.drawPolygon(tx, ty, 3);
                // Cap on top
                g2.fillRoundRect(cx - 3, by - 2, 6, 5, 2, 2);
                // Cow face (simple)
                g2.setColor(PRIMARY_GREEN);
                g2.drawOval(cx - 5, by + 14, 10, 8); // head
                g2.fillOval(cx - 2, by + 18, 2, 2); // left eye
                g2.fillOval(cx + 1, by + 18, 2, 2); // right eye
                // MILK text
                g2.setFont(new Font("SansSerif", Font.BOLD, 7));
                g2.drawString("MILK", bx + 4, by + 32);
                break;
            case "Fruits":
                // Line-art fruits (apple + leaf)
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - 10, cy - 6, 20, 20); // apple body
                // Stem
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(cx, cy - 6, cx + 2, cy - 13);
                // Leaf
                g2.drawOval(cx + 1, cy - 15, 8, 5);
                // Small highlight arc
                g2.setStroke(new BasicStroke(1f));
                g2.drawArc(cx - 5, cy - 2, 8, 10, 40, 80);
                break;
            case "Vegetables":
                // Line-art carrot
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                int[] crx = {cx - 7, cx + 7, cx};
                int[] cry = {cy - 6, cy - 6, cy + 14};
                g2.drawPolygon(crx, cry, 3);
                // Horizontal lines on carrot
                g2.drawLine(cx - 4, cy, cx + 4, cy);
                g2.drawLine(cx - 3, cy + 4, cx + 3, cy + 4);
                // Leaves
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawLine(cx, cy - 6, cx - 6, cy - 16);
                g2.drawLine(cx, cy - 6, cx, cy - 18);
                g2.drawLine(cx, cy - 6, cx + 6, cy - 16);
                break;
            case "Snacks":
                // Line-art bowl with chips
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                // Bowl
                g2.drawArc(cx - 14, cy - 2, 28, 20, 0, -180);
                g2.drawLine(cx - 14, cy - 2, cx + 14, cy - 2);
                // Chips sticking out
                int[] c1x = {cx - 8, cx - 4, cx - 2};
                int[] c1y = {cy - 2, cy - 14, cy - 2};
                g2.drawPolygon(c1x, c1y, 3);
                int[] c2x = {cx - 2, cx + 2, cx + 4};
                int[] c2y = {cy - 2, cy - 16, cy - 2};
                g2.drawPolygon(c2x, c2y, 3);
                int[] c3x = {cx + 4, cx + 8, cx + 10};
                int[] c3y = {cy - 2, cy - 12, cy - 2};
                g2.drawPolygon(c3x, c3y, 3);
                break;
            case "Meat":
                // Line-art steak
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - 14, cy - 8, 28, 18);
                // Marbling lines
                g2.setStroke(new BasicStroke(1f));
                g2.drawArc(cx - 6, cy - 4, 14, 10, 20, 140);
                g2.drawArc(cx - 4, cy - 2, 8, 6, 200, 140);
                // Bone circle
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx + 8, cy - 4, 6, 6);
                break;
            case "Tools":
                // Line-art cutting board + knife
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                // Cutting board
                g2.drawRoundRect(cx - 6, cy - 14, 16, 26, 4, 4);
                g2.drawOval(cx + 2, cy - 12, 4, 4); // hole
                // Knife
                g2.drawLine(cx - 12, cy - 8, cx - 12, cy + 10);
                g2.drawLine(cx - 12, cy + 10, cx - 8, cy + 10);
                g2.drawLine(cx - 8, cy + 10, cx - 8, cy - 4);
                g2.drawLine(cx - 8, cy - 4, cx - 12, cy - 8);
                // Knife handle
                g2.drawRect(cx - 13, cy - 14, 6, 6);
                break;
            default:
                // Line-art shopping bag
                g2.setColor(PRIMARY_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(cx - 10, cy - 4, 20, 18, 4, 4);
                g2.drawArc(cx - 6, cy - 12, 12, 12, 0, 180);
                break;
        }
    }

    JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.add(createWindowDots(), BorderLayout.WEST);
        RoundedButton searchIcon = new RoundedButton("\uD83D\uDD0D \u25BE");
        searchIcon.setPreferredSize(new Dimension(65, 32));
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchIcon.addActionListener(e -> showScreen("search"));
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        wrap.setOpaque(false);
        wrap.add(searchIcon);
        bar.add(wrap, BorderLayout.EAST);
        return bar;
    }

    JPanel createWindowDots() {
        JPanel dots = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dots.setOpaque(false);
        Color[] colors = {PRIMARY_GREEN, LIGHT_GREEN, PALE_GREEN};
        for (Color c : colors) {
            JPanel dot = new JPanel() {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c);
                    g2.fillOval(0, 0, 15, 15);
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(15, 15));
            dots.add(dot);
        }
        return dots;
    }

    RoundedButton makeBackButton(String target) {
        RoundedButton btn = new RoundedButton("") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 2;
                int ox = (getWidth() - d) / 2, oy = (getHeight() - d) / 2;
                g2.setColor(PRIMARY_GREEN);
                g2.fillOval(ox, oy, d, d);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawLine(cx + 5, cy, cx - 5, cy);
                g2.drawLine(cx - 5, cy, cx, cy - 5);
                g2.drawLine(cx - 5, cy, cx, cy + 5);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(38, 38));
        if (target.equals("cart_screen_back")) {
            btn.addActionListener(e -> showCartScreen());
        } else {
            btn.addActionListener(e -> showScreen(target));
        }
        return btn;
    }

    JButton makeIconButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 26));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setForeground(ICON_GREEN);
        return btn;
    }

    void showScreen(String name) { cardLayout.show(mainPanel, name); }

    void addToCart(Product p) {
        for (CartItem item : cart) {
            if (item.product.name.equals(p.name)) { item.qty++; return; }
        }
        cart.add(new CartItem(p));
    }

    // ═══════════════════════════════════════════════════════════════
    //  CUSTOM COMPONENTS
    // ═══════════════════════════════════════════════════════════════
    static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        int radius;
        RoundedPanel(int radius) { this.radius = radius; setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.dispose();
        }
    }

    static class RoundedButton extends JButton {
        private static final long serialVersionUID = 1L;
        private boolean hovered = false;
        RoundedButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? (hovered ? HOVER_GREEN : PRIMARY_GREEN) : PRIMARY_GREEN);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  DATA MODELS
    // ═══════════════════════════════════════════════════════════════
    static class Product {
        String name, description, aisle, category;
        double price;
        Product(String n, String d, double p, String a, String cat) { name = n; description = d; price = p; aisle = a; category = cat; }
    }

    static class CartItem {
        Product product;
        int qty;
        CartItem(Product p) { product = p; qty = 1; }
    }
}