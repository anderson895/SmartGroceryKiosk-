package app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SmartGroceryKiosk extends JFrame {

    // ── Color Palette (matching the PDF's olive/sage green theme) ──
    static final Color PRIMARY_GREEN = new Color(107, 114, 81);      // #6B7251
    static final Color LIGHT_GREEN = new Color(139, 148, 108);       // #8B946C
    static final Color PALE_GREEN = new Color(190, 197, 170);        // #BEC5AA
    static final Color BG_GRAY = new Color(235, 235, 235);           // #EBEBEB
    static final Color CARD_WHITE = new Color(255, 255, 255);
    static final Color TEXT_DARK = new Color(50, 50, 50);
    static final Color TEXT_MEDIUM = new Color(100, 100, 100);
    static final Color ICON_GREEN = new Color(90, 100, 70);

    // ── Data ──
    static final String[] CATEGORIES = {"Fruits", "Vegetables", "Tools", "Snacks", "Dairy", "Meat"};
    static Map<String, List<Product>> productsByCategory = new LinkedHashMap<>();
    static List<CartItem> cart = new ArrayList<>();

    // ── Panels ──
    CardLayout cardLayout;
    JPanel mainPanel;
    JPanel cartPanel;
    JLabel cartBadge;
    String currentCategory = "";

    public static void main(String[] args) {
        initProducts();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new SmartGroceryKiosk();
        });
    }

    static void initProducts() {
        productsByCategory.put("Fruits", Arrays.asList(
            new Product("Apple", "Fresh red apple, 1kg", 95.00, "Aisle 2"),
            new Product("Banana", "Lakatan banana, bundle", 65.00, "Aisle 2"),
            new Product("Mango", "Sweet Philippine mango", 120.00, "Aisle 2"),
            new Product("Grapes", "Seedless green grapes, 500g", 180.00, "Aisle 2"),
            new Product("Orange", "Valencia orange, 1kg", 110.00, "Aisle 2")
        ));
        productsByCategory.put("Vegetables", Arrays.asList(
            new Product("Tomato", "Fresh tomatoes, 1kg", 80.00, "Aisle 3"),
            new Product("Cabbage", "Green cabbage, whole", 55.00, "Aisle 3"),
            new Product("Carrot", "Fresh carrots, 500g", 60.00, "Aisle 3"),
            new Product("Onion", "Red onion, 1kg", 90.00, "Aisle 3"),
            new Product("Potato", "Washed potatoes, 1kg", 75.00, "Aisle 3")
        ));
        productsByCategory.put("Tools", Arrays.asList(
            new Product("Knife Set", "3-piece kitchen knife set", 350.00, "Aisle 6"),
            new Product("Cutting Board", "Bamboo cutting board", 220.00, "Aisle 6"),
            new Product("Peeler", "Stainless steel peeler", 85.00, "Aisle 6"),
            new Product("Tongs", "Silicone-tip kitchen tongs", 120.00, "Aisle 6")
        ));
        productsByCategory.put("Snacks", Arrays.asList(
            new Product("Chips", "Potato chips, 150g bag", 65.00, "Aisle 4"),
            new Product("Crackers", "Cream crackers, 200g", 55.00, "Aisle 4"),
            new Product("Cookies", "Chocolate chip cookies", 90.00, "Aisle 4"),
            new Product("Nuts", "Mixed nuts, 250g", 150.00, "Aisle 4"),
            new Product("Popcorn", "Microwave popcorn, 3-pack", 110.00, "Aisle 4")
        ));
        productsByCategory.put("Dairy", Arrays.asList(
            new Product("Magnolia", "Fresh milk, 1L", 85.00, "Aisle 1"),
            new Product("Nestle", "Full cream milk, 1L", 92.00, "Aisle 1"),
            new Product("Oatside", "Oat milk, barista blend 1L", 165.00, "Aisle 1"),
            new Product("Cheese", "Cheddar cheese block, 250g", 140.00, "Aisle 1"),
            new Product("Yogurt", "Greek yogurt, 500g", 125.00, "Aisle 1")
        ));
        productsByCategory.put("Meat", Arrays.asList(
            new Product("Chicken", "Whole chicken, 1.2kg", 220.00, "Aisle 5"),
            new Product("Pork Chop", "Pork chop, 500g", 185.00, "Aisle 5"),
            new Product("Ground Beef", "Lean ground beef, 500g", 250.00, "Aisle 5"),
            new Product("Hotdog", "Jumbo hotdog, 500g", 130.00, "Aisle 5"),
            new Product("Bacon", "Smoked bacon, 250g", 175.00, "Aisle 5")
        ));
    }

    public SmartGroceryKiosk() {
        setTitle("JAVA.R Smart Grocery Kiosk");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 750);
        setMinimumSize(new Dimension(460, 700));
        setLocationRelativeTo(null);
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

    // ═══════════════════════════════════════════════════════════════
    //  WELCOME SCREEN (Page 1)
    // ═══════════════════════════════════════════════════════════════
    JPanel createWelcomePanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Window dots + search icon row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(createWindowDots(), BorderLayout.WEST);
        topRow.add(createSearchIcon(), BorderLayout.EAST);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(topRow);
        card.add(Box.createVerticalStrut(25));

        // Welcome text
        JLabel welcome = new JLabel("Welcome to");
        welcome.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
        welcome.setForeground(TEXT_DARK);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(welcome);

        JLabel brand = new JLabel("JAVA.R");
        brand.setFont(new Font("Serif", Font.PLAIN, 42));
        brand.setForeground(TEXT_DARK);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(brand);

        JLabel sub = new JLabel("S M A R T   G R O C E R Y");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(TEXT_MEDIUM);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(40));

        // Buttons
        String[] labels = {"BROWSE CATEGORIES", "SEARCH PRODUCTS", "PROMOTIONS"};
        String[] targets = {"categories", "search", "promotions"};
        for (int i = 0; i < labels.length; i++) {
            RoundedButton btn = new RoundedButton(labels[i]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(300, 50));
            final String target = targets[i];
            btn.addActionListener(e -> showScreen(target));
            card.add(btn);
            card.add(Box.createVerticalStrut(15));
        }

        card.add(Box.createVerticalGlue());

        // Bottom icons (cart + location)
        JPanel bottomIcons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomIcons.setOpaque(false);
        JButton cartBtn = createIconButton("\uD83D\uDED2", "Cart");
        cartBtn.addActionListener(e -> showCartScreen());
        bottomIcons.add(cartBtn);
        JLabel locIcon = new JLabel("\uD83D\uDCCD");
        locIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        locIcon.setForeground(ICON_GREEN);
        bottomIcons.add(locIcon);
        bottomIcons.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(bottomIcons);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ═══════════════════════════════════════════════════════════════
    //  BROWSE CATEGORIES SCREEN (Page 2)
    // ═══════════════════════════════════════════════════════════════
    JPanel createCategoriesPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(createWindowDots(), BorderLayout.WEST);
        topRow.add(createSearchIcon(), BorderLayout.EAST);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(topRow);
        card.add(Box.createVerticalStrut(20));

        // Title
        JLabel title = new JLabel("BROWSE CATEGORIES", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(30));

        // Category grid 3x2
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(400, 350));
        grid.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] icons = {"\uD83C\uDF4E", "\uD83E\uDD66", "\uD83D\uDD27", "\uD83C\uDF7F", "\uD83E\uDDC8", "\uD83E\uDD69"};
        for (int i = 0; i < CATEGORIES.length; i++) {
            JPanel catCard = createCategoryCard(CATEGORIES[i], icons[i]);
            grid.add(catCard);
        }
        card.add(grid);
        card.add(Box.createVerticalGlue());

        // Back button
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backRow.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("< Back");
        backBtn.setMaximumSize(new Dimension(100, 35));
        backBtn.addActionListener(e -> showScreen("welcome"));
        backRow.add(backBtn);
        backRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(backRow);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    JPanel createCategoryCard(String name, String emoji) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon circle
        JLabel icon = new JLabel(emoji, SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setPreferredSize(new Dimension(80, 80));

        // Draw a circle border around the icon
        JPanel iconHolder = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PALE_GREEN);
                g2.setStroke(new BasicStroke(2));
                int size = Math.min(getWidth(), getHeight()) - 4;
                g2.drawOval((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
            }
        };
        iconHolder.setOpaque(false);
        iconHolder.setPreferredSize(new Dimension(90, 90));
        iconHolder.setMaximumSize(new Dimension(90, 90));
        iconHolder.add(icon);
        iconHolder.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(name, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconHolder);
        panel.add(Box.createVerticalStrut(5));
        panel.add(label);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showProductList(name);
            }
        });
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════
    //  SEARCH PRODUCTS SCREEN
    // ═══════════════════════════════════════════════════════════════
    JPanel createSearchPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(createWindowDots(), BorderLayout.WEST);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(topRow);
        card.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("SEARCH PRODUCTS", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        // Search bar
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.setMaximumSize(new Dimension(380, 42));
        searchRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PALE_GREEN, 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        RoundedButton searchBtn = new RoundedButton("Search");
        searchBtn.setPreferredSize(new Dimension(90, 38));

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);

        ActionListener searchAction = e -> {
            String query = searchField.getText().trim().toLowerCase();
            resultsPanel.removeAll();
            if (query.isEmpty()) {
                resultsPanel.revalidate();
                resultsPanel.repaint();
                return;
            }
            boolean found = false;
            for (Map.Entry<String, List<Product>> entry : productsByCategory.entrySet()) {
                for (Product p : entry.getValue()) {
                    if (p.name.toLowerCase().contains(query) || p.description.toLowerCase().contains(query)) {
                        resultsPanel.add(createProductRow(p));
                        resultsPanel.add(Box.createVerticalStrut(8));
                        found = true;
                    }
                }
            }
            if (!found) {
                JLabel noResult = new JLabel("No products found.", SwingConstants.CENTER);
                noResult.setFont(new Font("SansSerif", Font.ITALIC, 14));
                noResult.setForeground(TEXT_MEDIUM);
                noResult.setAlignmentX(Component.CENTER_ALIGNMENT);
                resultsPanel.add(Box.createVerticalStrut(20));
                resultsPanel.add(noResult);
            }
            resultsPanel.revalidate();
            resultsPanel.repaint();
        };
        searchBtn.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchBtn, BorderLayout.EAST);
        card.add(searchRow);
        card.add(Box.createVerticalStrut(15));
        card.add(scroll);
        card.add(Box.createVerticalGlue());

        // Back
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backRow.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("< Back");
        backBtn.addActionListener(e -> showScreen("welcome"));
        backRow.add(backBtn);
        backRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(backRow);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ═══════════════════════════════════════════════════════════════
    //  PROMOTIONS SCREEN
    // ═══════════════════════════════════════════════════════════════
    JPanel createPromotionsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(createWindowDots(), BorderLayout.WEST);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(topRow);
        card.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("PROMOTIONS", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        // Promotion cards
        String[][] promos = {
            {"Buy 1 Get 1 Free!", "All Magnolia Fresh Milk 1L", "Until April 30, 2026"},
            {"20% OFF", "Selected fruits and vegetables", "Weekends only"},
            {"\u20B120 Discount", "On orders above \u20B1200", "Use code: SMART20"}
        };
        for (String[] promo : promos) {
            JPanel promoCard = new JPanel();
            promoCard.setLayout(new BoxLayout(promoCard, BoxLayout.Y_AXIS));
            promoCard.setBackground(new Color(245, 247, 240));
            promoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PALE_GREEN, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            promoCard.setMaximumSize(new Dimension(380, 90));
            promoCard.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel promoTitle = new JLabel(promo[0]);
            promoTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
            promoTitle.setForeground(PRIMARY_GREEN);

            JLabel promoDesc = new JLabel(promo[1]);
            promoDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
            promoDesc.setForeground(TEXT_DARK);

            JLabel promoDate = new JLabel(promo[2]);
            promoDate.setFont(new Font("SansSerif", Font.ITALIC, 11));
            promoDate.setForeground(TEXT_MEDIUM);

            promoCard.add(promoTitle);
            promoCard.add(Box.createVerticalStrut(3));
            promoCard.add(promoDesc);
            promoCard.add(Box.createVerticalStrut(2));
            promoCard.add(promoDate);

            card.add(promoCard);
            card.add(Box.createVerticalStrut(12));
        }

        card.add(Box.createVerticalGlue());

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backRow.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("< Back");
        backBtn.addActionListener(e -> showScreen("welcome"));
        backRow.add(backBtn);
        backRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(backRow);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ═══════════════════════════════════════════════════════════════
    //  PRODUCT LIST SCREEN (Page 3)
    // ═══════════════════════════════════════════════════════════════
    void showProductList(String category) {
        currentCategory = category;
        String panelName = "products_" + category;

        // Remove old panel if exists
        for (Component c : mainPanel.getComponents()) {
            if (panelName.equals(c.getName())) {
                mainPanel.remove(c);
                break;
            }
        }

        JPanel outer = new JPanel(new BorderLayout());
        outer.setName(panelName);
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top row with dots, aisle label, and search
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(createWindowDots(), BorderLayout.WEST);

        String aisle = productsByCategory.get(category).get(0).aisle;
        JLabel aisleLabel = new JLabel(aisle, SwingConstants.CENTER);
        aisleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        aisleLabel.setForeground(TEXT_MEDIUM);
        topRow.add(aisleLabel, BorderLayout.CENTER);

        topRow.add(createSearchIcon(), BorderLayout.EAST);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(topRow);
        card.add(Box.createVerticalStrut(5));

        // Back button row + category icon
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        RoundedButton backBtn = new RoundedButton("<");
        backBtn.setPreferredSize(new Dimension(40, 40));
        backBtn.addActionListener(e -> showScreen("categories"));
        headerRow.add(backBtn, BorderLayout.WEST);

        // Category emoji + name
        String[] icons = {"\uD83C\uDF4E", "\uD83E\uDD66", "\uD83D\uDD27", "\uD83C\uDF7F", "\uD83E\uDDC8", "\uD83E\uDD69"};
        int catIdx = Arrays.asList(CATEGORIES).indexOf(category);
        JPanel catHeader = new JPanel();
        catHeader.setLayout(new BoxLayout(catHeader, BoxLayout.Y_AXIS));
        catHeader.setOpaque(false);
        JLabel catIcon = new JLabel(icons[catIdx], SwingConstants.CENTER);
        catIcon.setFont(new Font("SansSerif", Font.PLAIN, 32));
        catIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel catName = new JLabel(category, SwingConstants.CENTER);
        catName.setFont(new Font("Serif", Font.PLAIN, 22));
        catName.setForeground(TEXT_DARK);
        catName.setAlignmentX(Component.CENTER_ALIGNMENT);
        catHeader.add(catIcon);
        catHeader.add(catName);
        headerRow.add(catHeader, BorderLayout.CENTER);
        headerRow.add(Box.createHorizontalStrut(40), BorderLayout.EAST); // balance

        card.add(headerRow);
        card.add(Box.createVerticalStrut(10));

        // Product list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (Product p : productsByCategory.get(category)) {
            listPanel.add(createProductRow(p));
            listPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(scroll);

        outer.add(card, BorderLayout.CENTER);
        mainPanel.add(outer, panelName);
        cardLayout.show(mainPanel, panelName);
    }

    JPanel createProductRow(Product p) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Product icon placeholder
        JPanel iconPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 247, 240));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                g2.setColor(PALE_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(60, 60));
        JLabel prodEmoji = new JLabel("\uD83D\uDED2");
        prodEmoji.setFont(new Font("SansSerif", Font.PLAIN, 22));
        iconPanel.add(prodEmoji);

        // Name + description
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel name = new JLabel(p.name);
        name.setFont(new Font("SansSerif", Font.BOLD, 14));
        name.setForeground(TEXT_DARK);
        JLabel desc = new JLabel(p.description);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 11));
        desc.setForeground(TEXT_MEDIUM);
        JLabel aisleL = new JLabel(p.aisle);
        aisleL.setFont(new Font("SansSerif", Font.ITALIC, 10));
        aisleL.setForeground(LIGHT_GREEN);
        info.add(name);
        info.add(desc);
        info.add(aisleL);

        // Price + Add to cart
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        JLabel price = new JLabel(String.format("\u20B1%.2f", p.price));
        price.setFont(new Font("SansSerif", Font.BOLD, 13));
        price.setForeground(TEXT_DARK);
        price.setAlignmentX(Component.RIGHT_ALIGNMENT);

        RoundedButton addBtn = new RoundedButton("Add to cart");
        addBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        addBtn.setPreferredSize(new Dimension(95, 30));
        addBtn.setMaximumSize(new Dimension(95, 30));
        addBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        addBtn.addActionListener(e -> {
            addToCart(p);
            JOptionPane.showMessageDialog(this,
                p.name + " added to cart!",
                "Added", JOptionPane.INFORMATION_MESSAGE);
        });

        rightPanel.add(price);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(addBtn);

        row.add(iconPanel, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════
    //  SHOPPING CART SCREEN (Page 4)
    // ═══════════════════════════════════════════════════════════════
    void showCartScreen() {
        String panelName = "cart_screen";
        for (Component c : mainPanel.getComponents()) {
            if (panelName.equals(c.getName())) {
                mainPanel.remove(c);
                break;
            }
        }

        JPanel outer = new JPanel(new BorderLayout());
        outer.setName(panelName);
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(createWindowDots(), BorderLayout.WEST);
        topRow.add(createSearchIcon(), BorderLayout.EAST);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(topRow);
        card.add(Box.createVerticalStrut(5));

        // Back + cart icon
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        RoundedButton backBtn = new RoundedButton("<");
        backBtn.setPreferredSize(new Dimension(40, 40));
        backBtn.addActionListener(e -> showScreen("welcome"));
        headerRow.add(backBtn, BorderLayout.WEST);

        JPanel cartHeader = new JPanel();
        cartHeader.setLayout(new BoxLayout(cartHeader, BoxLayout.Y_AXIS));
        cartHeader.setOpaque(false);
        JLabel cartIcon = new JLabel("\uD83D\uDED2", SwingConstants.CENTER);
        cartIcon.setFont(new Font("SansSerif", Font.PLAIN, 36));
        cartIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel cartTitle = new JLabel("Your Shopping Cart", SwingConstants.CENTER);
        cartTitle.setFont(new Font("Serif", Font.PLAIN, 20));
        cartTitle.setForeground(TEXT_DARK);
        cartTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartHeader.add(cartIcon);
        cartHeader.add(cartTitle);
        headerRow.add(cartHeader, BorderLayout.CENTER);
        headerRow.add(Box.createHorizontalStrut(40), BorderLayout.EAST);

        card.add(headerRow);
        card.add(Box.createVerticalStrut(10));

        if (cart.isEmpty()) {
            JLabel empty = new JLabel("Your cart is empty.", SwingConstants.CENTER);
            empty.setFont(new Font("SansSerif", Font.ITALIC, 14));
            empty.setForeground(TEXT_MEDIUM);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(Box.createVerticalStrut(40));
            card.add(empty);
            card.add(Box.createVerticalGlue());
        } else {
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setOpaque(false);

            for (CartItem item : cart) {
                listPanel.add(createCartRow(item));
                listPanel.add(Box.createVerticalStrut(8));
            }

            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            card.add(scroll);
            card.add(Box.createVerticalStrut(10));

            // Subtotal
            double subtotal = 0;
            for (CartItem item : cart) subtotal += item.product.price * item.qty;
            double discount = subtotal >= 200 ? 20.0 : 0.0;
            double total = subtotal - discount;

            // Bottom bar
            JPanel bottomBar = new JPanel(new BorderLayout(10, 5));
            bottomBar.setOpaque(false);
            bottomBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            bottomBar.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel subtotalPanel = new JPanel();
            subtotalPanel.setLayout(new BoxLayout(subtotalPanel, BoxLayout.Y_AXIS));
            subtotalPanel.setOpaque(false);

            JLabel subLabel = new JLabel(String.format("Subtotal: \u20B1%.2f", subtotal));
            subLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            subLabel.setForeground(TEXT_DARK);
            if (discount > 0) {
                JLabel discLabel = new JLabel(String.format("Discount: -\u20B1%.2f", discount));
                discLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                discLabel.setForeground(PRIMARY_GREEN);
                subtotalPanel.add(discLabel);
            }
            JLabel totalLabel = new JLabel(String.format("Total: \u20B1%.2f", total));
            totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            totalLabel.setForeground(TEXT_DARK);
            subtotalPanel.add(subLabel);
            subtotalPanel.add(totalLabel);

            RoundedButton removeBtn = new RoundedButton("Remove All");
            removeBtn.setPreferredSize(new Dimension(110, 35));
            removeBtn.addActionListener(e -> {
                cart.clear();
                showCartScreen();
            });

            bottomBar.add(subtotalPanel, BorderLayout.WEST);
            bottomBar.add(removeBtn, BorderLayout.EAST);

            card.add(bottomBar);
            card.add(Box.createVerticalStrut(10));

            // Proceed to checkout
            double finalTotal = total;
            RoundedButton checkoutBtn = new RoundedButton("Proceed to checkout");
            checkoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            checkoutBtn.setMaximumSize(new Dimension(250, 45));
            checkoutBtn.addActionListener(e -> showPaymentScreen(finalTotal));
            card.add(checkoutBtn);
        }

        outer.add(card, BorderLayout.CENTER);
        mainPanel.add(outer, panelName);
        cardLayout.show(mainPanel, panelName);
    }

    JPanel createCartRow(CartItem item) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Icon
        JPanel iconPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 247, 240));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(55, 55));
        JLabel emoji = new JLabel("\uD83D\uDED2");
        emoji.setFont(new Font("SansSerif", Font.PLAIN, 20));
        iconPanel.add(emoji);

        // Info
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

        // Price + qty controls
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JLabel price = new JLabel(String.format("\u20B1%.2f", item.product.price * item.qty));
        price.setFont(new Font("SansSerif", Font.BOLD, 13));
        price.setForeground(TEXT_DARK);
        price.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        qtyPanel.setOpaque(false);
        RoundedButton minusBtn = new RoundedButton("\u2212");
        minusBtn.setPreferredSize(new Dimension(35, 30));
        minusBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel qtyLabel = new JLabel(String.valueOf(item.qty), SwingConstants.CENTER);
        qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        qtyLabel.setPreferredSize(new Dimension(25, 30));
        RoundedButton plusBtn = new RoundedButton("+");
        plusBtn.setPreferredSize(new Dimension(35, 30));
        plusBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        minusBtn.addActionListener(e -> {
            if (item.qty > 1) item.qty--;
            else cart.remove(item);
            showCartScreen();
        });
        plusBtn.addActionListener(e -> {
            item.qty++;
            showCartScreen();
        });

        qtyPanel.add(minusBtn);
        qtyPanel.add(qtyLabel);
        qtyPanel.add(plusBtn);
        qtyPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(price);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(qtyPanel);

        row.add(iconPanel, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════
    //  PAYMENT SCREEN (Page 5) - QR Code Simulation
    // ═══════════════════════════════════════════════════════════════
    void showPaymentScreen(double total) {
        String panelName = "payment";
        for (Component c : mainPanel.getComponents()) {
            if (panelName.equals(c.getName())) {
                mainPanel.remove(c);
                break;
            }
        }

        JPanel outer = new JPanel(new BorderLayout());
        outer.setName(panelName);
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(createWindowDots(), BorderLayout.WEST);
        topRow.add(createSearchIcon(), BorderLayout.EAST);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(topRow);
        card.add(Box.createVerticalStrut(5));

        // Back
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        RoundedButton backBtn = new RoundedButton("<");
        backBtn.setPreferredSize(new Dimension(40, 40));
        backBtn.addActionListener(e -> showCartScreen());
        headerRow.add(backBtn, BorderLayout.WEST);

        // Money icon
        JLabel moneyIcon = new JLabel("\uD83D\uDCB5", SwingConstants.CENTER);
        moneyIcon.setFont(new Font("SansSerif", Font.PLAIN, 36));
        headerRow.add(moneyIcon, BorderLayout.CENTER);
        headerRow.add(Box.createHorizontalStrut(40), BorderLayout.EAST);
        card.add(headerRow);

        JLabel payTitle = new JLabel("COMPLETE PAYMENT", SwingConstants.CENTER);
        payTitle.setFont(new Font("Serif", Font.BOLD, 22));
        payTitle.setForeground(TEXT_DARK);
        payTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(payTitle);

        JLabel totalLabel = new JLabel(String.format("Total: \u20B1%.2f", total), SwingConstants.CENTER);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(PRIMARY_GREEN);
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(10));
        card.add(totalLabel);
        card.add(Box.createVerticalStrut(20));

        // QR Code simulation
        JPanel qrPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawQRCode((Graphics2D) g, getWidth(), getHeight());
            }
        };
        qrPanel.setOpaque(false);
        qrPanel.setPreferredSize(new Dimension(220, 220));
        qrPanel.setMaximumSize(new Dimension(220, 220));
        qrPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(qrPanel);
        card.add(Box.createVerticalStrut(25));

        // Generate receipt
        RoundedButton receiptBtn = new RoundedButton("Generate Receipt");
        receiptBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptBtn.setMaximumSize(new Dimension(220, 45));
        receiptBtn.addActionListener(e -> showReceiptScreen(total));
        card.add(receiptBtn);

        card.add(Box.createVerticalGlue());

        outer.add(card, BorderLayout.CENTER);
        mainPanel.add(outer, panelName);
        cardLayout.show(mainPanel, panelName);
    }

    void drawQRCode(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cellSize = 10;
        int gridSize = 21;
        int totalSize = gridSize * cellSize;
        int offsetX = (w - totalSize) / 2;
        int offsetY = (h - totalSize) / 2;

        Random rand = new Random(42); // Fixed seed for consistent look
        g2.setColor(Color.BLACK);

        // Draw finder patterns (3 corners)
        drawFinderPattern(g2, offsetX, offsetY, cellSize);
        drawFinderPattern(g2, offsetX + 14 * cellSize, offsetY, cellSize);
        drawFinderPattern(g2, offsetX, offsetY + 14 * cellSize, cellSize);

        // Fill random data
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (isInFinderArea(row, col)) continue;
                if (rand.nextBoolean()) {
                    g2.fillRect(offsetX + col * cellSize, offsetY + row * cellSize, cellSize, cellSize);
                }
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
        if (row < 8 && col < 8) return true;
        if (row < 8 && col > 12) return true;
        if (row > 12 && col < 8) return true;
        return false;
    }

    // ═══════════════════════════════════════════════════════════════
    //  RECEIPT SCREEN (Page 6)
    // ═══════════════════════════════════════════════════════════════
    void showReceiptScreen(double total) {
        String panelName = "receipt";
        for (Component c : mainPanel.getComponents()) {
            if (panelName.equals(c.getName())) {
                mainPanel.remove(c);
                break;
            }
        }

        JPanel outer = new JPanel(new BorderLayout());
        outer.setName(panelName);
        outer.setBackground(BG_GRAY);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(CARD_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(createWindowDots(), BorderLayout.WEST);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        card.add(topRow);
        card.add(Box.createVerticalStrut(15));

        // Brand
        JLabel brand = new JLabel("JAVA.R", SwingConstants.CENTER);
        brand.setFont(new Font("Serif", Font.ITALIC, 36));
        brand.setForeground(TEXT_DARK);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(brand);

        JLabel smartLabel = new JLabel("S M A R T   G R O C E R Y", SwingConstants.CENTER);
        smartLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        smartLabel.setForeground(TEXT_MEDIUM);
        smartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(smartLabel);
        card.add(Box.createVerticalStrut(8));

        // Dashed line
        card.add(createDashedLine());
        card.add(Box.createVerticalStrut(8));

        // Date + Transaction
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

        // Items
        double subtotal = 0;
        for (CartItem item : cart) {
            double lineTotal = item.product.price * item.qty;
            subtotal += lineTotal;
            String line = String.format("%-20s x%d    \u20B1%.2f",
                item.product.name, item.qty, lineTotal);
            addReceiptLine(card, line);
        }

        card.add(Box.createVerticalStrut(5));
        double discount = subtotal >= 200 ? 20.0 : 0.0;

        addReceiptLine(card, String.format("Subtotal:                   \u20B1%.2f", subtotal));
        if (discount > 0) {
            addReceiptLine(card, String.format("Discount:                  -\u20B1%.2f", discount));
        }
        card.add(createDashedLine());

        JLabel totalLine = new JLabel(String.format("TOTAL:                      \u20B1%.2f", total));
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

        // Done button
        RoundedButton doneBtn = new RoundedButton("Done");
        doneBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneBtn.setMaximumSize(new Dimension(150, 40));
        doneBtn.addActionListener(e -> {
            cart.clear();
            showScreen("welcome");
        });
        card.add(doneBtn);

        card.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        outer.add(scroll, BorderLayout.CENTER);

        mainPanel.add(outer, panelName);
        cardLayout.show(mainPanel, panelName);
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
    //  UTILITIES
    // ═══════════════════════════════════════════════════════════════
    void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }

    void addToCart(Product p) {
        for (CartItem item : cart) {
            if (item.product.name.equals(p.name)) {
                item.qty++;
                return;
            }
        }
        cart.add(new CartItem(p));
    }

    JPanel createWindowDots() {
        JPanel dots = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dots.setOpaque(false);
        Color[] dotColors = {PRIMARY_GREEN, LIGHT_GREEN, PALE_GREEN};
        for (Color c : dotColors) {
            JPanel dot = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c);
                    g2.fillOval(0, 0, 14, 14);
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(14, 14));
            dots.add(dot);
        }
        return dots;
    }

    JPanel createSearchIcon() {
        RoundedButton searchIconBtn = new RoundedButton("\uD83D\uDD0D \u25BE");
        searchIconBtn.setPreferredSize(new Dimension(70, 35));
        searchIconBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchIconBtn.addActionListener(e -> showScreen("search"));
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(searchIconBtn);
        return wrapper;
    }

    JButton createIconButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 24));
        btn.setToolTipText(tooltip);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setForeground(ICON_GREEN);
        return btn;
    }

    // ═══════════════════════════════════════════════════════════════
    //  CUSTOM COMPONENTS
    // ═══════════════════════════════════════════════════════════════

    // Rounded panel with soft shadow
    static class RoundedPanel extends JPanel {
        int radius;
        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Shadow
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, radius, radius);
            // Card
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, radius, radius);
            g2.dispose();
        }
    }

    // Rounded button matching the olive green style
    static class RoundedButton extends JButton {
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
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hovered ? LIGHT_GREEN : PRIMARY_GREEN);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  DATA MODELS
    // ═══════════════════════════════════════════════════════════════
    static class Product {
        String name, description, aisle;
        double price;
        Product(String name, String description, double price, String aisle) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.aisle = aisle;
        }
    }

    static class CartItem {
        Product product;
        int qty;
        CartItem(Product product) {
            this.product = product;
            this.qty = 1;
        }
    }
}
