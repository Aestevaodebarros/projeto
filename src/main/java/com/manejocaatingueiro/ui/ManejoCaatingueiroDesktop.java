package com.manejocaatingueiro.ui;

import com.manejocaatingueiro.ManejoCaatingueiroApplication;
import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.model.Localidade;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.service.ClimaService;
import com.manejocaatingueiro.service.ComunidadService;
import com.manejocaatingueiro.service.PlantioService;
import com.manejocaatingueiro.service.ReservatorioService;
import com.manejocaatingueiro.service.UsuarioService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.WebApplicationType;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ManejoCaatingueiroDesktop extends Application {
    private static ConfigurableApplicationContext springContext;

    private ClimaService climaService;
    private PlantioService plantioService;
    private ReservatorioService reservatorioService;
    private ComunidadService comunidadService;
    private UsuarioService usuarioService;
    private BorderPane root;
    private VBox content;
    private Label feedback;
    private ComboBox<Localidade> localidadeCombo;
    private Button activeNavButton;
    private int localidadeId = 1;
    private final long usuarioId = 1L;

    public static void main(String[] args) {
        springContext = new SpringApplicationBuilder(ManejoCaatingueiroApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        climaService = springContext.getBean(ClimaService.class);
        plantioService = springContext.getBean(PlantioService.class);
        reservatorioService = springContext.getBean(ReservatorioService.class);
        comunidadService = springContext.getBean(ComunidadService.class);
        usuarioService = springContext.getBean(UsuarioService.class);

        root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(buildSidebar());
        root.setTop(buildHeader());
        content = new VBox(20);
        content.setPadding(new Insets(28));
        root.setCenter(wrap(content));
        root.setBottom(buildFooter());

        showDashboard();
        Scene scene = new Scene(root, 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        stage.setTitle("Manejo Caatingueiro");
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    private Node buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 20, 16));
        sidebar.setPrefWidth(245);

        Label brand = new Label("🌵  Manejo\n     Caatingueiro");
        brand.getStyleClass().add("brand");
        Label section = new Label("ÁREA DO PRODUTOR");
        section.getStyleClass().add("nav-section");
        sidebar.getChildren().addAll(
                brand,
                new Separator(),
                section,
                navButton("Visão geral", this::showDashboard, true),
                navButton("Plantio e água", this::showPlanting, false),
                navButton("Reservatórios", this::showReservoirs, false),
                navButton("Comunidade", this::showCommunity, false)
        );
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label consultive = new Label("Informação consultiva.\nA decisão é sempre sua.");
        consultive.getStyleClass().add("sidebar-note");
        sidebar.getChildren().addAll(spacer, consultive);
        return sidebar;
    }

    private Node buildHeader() {
        HBox header = new HBox(14);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 28, 18, 28));

        VBox greeting = new VBox(3);
        greeting.getChildren().addAll(
                new Label("Bem-vindo(a), agricultor(a)"),
                new Label("Acompanhe as condições da sua propriedade")
        );
        greeting.getChildren().get(0).getStyleClass().add("header-title");
        greeting.getChildren().get(1).getStyleClass().add("muted");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        localidadeCombo = new ComboBox<>(FXCollections.observableArrayList(climaService.listarLocalidades()));
        localidadeCombo.setPrefWidth(250);
        localidadeCombo.setPromptText("Escolha uma localidade");
        localidadeCombo.getSelectionModel().selectFirst();
        localidadeCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Localidade localidade) {
                return localidade == null ? "" : localidade.getNome();
            }

            @Override
            public Localidade fromString(String value) {
                return null;
            }
        });
        localidadeCombo.setOnAction(event -> {
            Localidade selected = localidadeCombo.getValue();
            if (selected != null) {
                localidadeId = selected.getId();
                showDashboard();
            }
        });
        Button refresh = new Button("Atualizar");
        refresh.getStyleClass().add("button-secondary");
        refresh.setOnAction(event -> showDashboard());
        header.getChildren().addAll(greeting, spacer, localidadeCombo, refresh);
        return header;
    }

    private Node buildFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("footer");
        footer.setPadding(new Insets(10, 28, 10, 28));
        feedback = new Label("Dados de demonstração — valide as fontes locais antes de decidir.");
        feedback.getStyleClass().add("muted");
        footer.getChildren().add(feedback);
        return footer;
    }

    private Button navButton(String title, Runnable action, boolean active) {
        Button button = new Button(title);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add(active ? "nav-button-active" : "nav-button");
        if (active) {
            activeNavButton = button;
        }
        button.setOnAction(event -> {
            if (activeNavButton != null) {
                activeNavButton.getStyleClass().remove("nav-button-active");
                activeNavButton.getStyleClass().add("nav-button");
            }
            button.getStyleClass().remove("nav-button");
            button.getStyleClass().add("nav-button-active");
            activeNavButton = button;
            action.run();
        });
        return button;
    }

    private ScrollPane wrap(Node node) {
        ScrollPane scrollPane = new ScrollPane(node);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        return scrollPane;
    }

    private void showDashboard() {
        try {
            Map<String, Object> climate = climaService.diagnostico(localidadeId);
            Usuario user = requireDemoUser();
            Map<String, Object> water = plantioService.avaliarAptidao(localidadeId, user);
            content = new VBox(20);
            content.setPadding(new Insets(28));

            Label title = new Label("Visão geral");
            title.getStyleClass().add("page-title");
            Label subtitle = new Label("Um retrato simples da chuva, da água disponível e das opções de plantio.");
            subtitle.getStyleClass().add("muted");
            content.getChildren().addAll(title, subtitle, buildWeatherSummary(climate, water));

            HBox metrics = new HBox(16,
                    metricCard("CHUVA NO MÊS", value(climate.get("chuvaAcumuladaMm")) + " mm",
                            "Normal para o período: " + value(climate.get("mediaHistoricaMm")) + " mm", "blue"),
                    metricCard("AUTONOMIA HÍDRICA", value(water.get("autonomiaDias")) + " dias",
                            "Estimativa da propriedade", "olive"),
                    metricCard("RESERVATÓRIOS", String.valueOf(reservatorioService.listarPorLocalidade(localidadeId).size()),
                            "Registros nesta localidade", "ochre")
            );
            metrics.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));

            HBox details = new HBox(16, buildRainChart(climate), buildCropSummary(water));
            HBox.setHgrow(details.getChildren().get(0), Priority.ALWAYS);
            HBox.setHgrow(details.getChildren().get(1), Priority.ALWAYS);
            content.getChildren().addAll(metrics, details);
            root.setCenter(wrap(content));
            showFeedback("Fonte: " + value(climate.get("fonte")) + " · Atualizado " + now());
        } catch (RuntimeException error) {
            showError("Não foi possível carregar o painel: " + error.getMessage());
        }
    }

    private Node buildWeatherSummary(Map<String, Object> climate, Map<String, Object> water) {
        HBox banner = new HBox(14);
        banner.getStyleClass().add("summary-banner");
        banner.setAlignment(Pos.CENTER_LEFT);
        VBox text = new VBox(5);
        text.getChildren().addAll(
                new Label(weatherTitle(String.valueOf(climate.get("status")))),
                new Label("A oferta de água é uma estimativa. Use também suas observações e a chuva no pluviômetro.")
        );
        text.getChildren().get(0).getStyleClass().add("summary-title");
        text.getChildren().get(1).getStyleClass().add("muted");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label status = new Label(String.valueOf(water.get("status")));
        status.getStyleClass().add("status-pill");
        banner.getChildren().addAll(new Label("☀"), text, spacer, status);
        return banner;
    }

    private Node buildRainChart(Map<String, Object> climate) {
        VBox card = sectionCard("Chuva registrada", "Eventos recentes em milímetros");
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Data");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Chuva (mm)");
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Object rawSeries = climate.get("serie");
        if (rawSeries instanceof List<?> records) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
            for (Object record : records) {
                if (record instanceof Precipitacao rain) {
                    String date = rain.getData().atZone(ZoneId.systemDefault()).format(formatter);
                    series.getData().add(new XYChart.Data<>(date, rain.getVolumeMm()));
                }
            }
        }
        chart.getData().add(series);
        card.getChildren().add(chart);
        return card;
    }

    private Node buildCropSummary(Map<String, Object> water) {
        VBox card = sectionCard("Viabilidade das culturas", "Indicação consultiva para a sua propriedade");
        Object rawCrops = water.get("culturas");
        if (rawCrops instanceof List<?> crops) {
            for (Object item : crops) {
                if (item instanceof Map<?, ?> crop) {
                    String name = String.valueOf(crop.get("nome"));
                    String status = String.valueOf(crop.get("status"));
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    Label cropName = new Label(name);
                    cropName.getStyleClass().add("crop-name");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    Label badge = new Label(cropLabel(status));
                    badge.getStyleClass().add(statusClass(status));
                    row.getChildren().addAll(cropName, spacer, badge);
                    card.getChildren().addAll(row, new Label(String.valueOf(crop.get("mensagem"))));
                    card.getChildren().get(card.getChildren().size() - 1).getStyleClass().add("muted");
                    card.getChildren().add(new Separator());
                }
            }
        }
        Label source = new Label("Fonte agronômica: " + value(water.get("fonteDadosAgronomicos")));
        source.getStyleClass().add("source-note");
        card.getChildren().add(source);
        return card;
    }

    private void showPlanting() {
        try {
            Map<String, Object> result = plantioService.avaliarAptidao(localidadeId, requireDemoUser());
            content = new VBox(18);
            content.setPadding(new Insets(28));
            Label title = new Label("Plantio e autonomia hídrica");
            title.getStyleClass().add("page-title");
            Label description = new Label("Estimativa consultiva com base no consumo cadastrado e na disponibilidade hídrica.");
            description.getStyleClass().add("muted");
            content.getChildren().addAll(title, description);
            VBox autonomy = metricCard("AUTONOMIA ESTIMADA", value(result.get("autonomiaDias")) + " dias",
                    "Status: " + value(result.get("status")), "olive");
            content.getChildren().add(autonomy);

            Object crops = result.get("culturas");
            if (crops instanceof List<?> list) {
                for (Object value : list) {
                    if (value instanceof Map<?, ?> crop) {
                        VBox cropCard = sectionCard(String.valueOf(crop.get("nome")), cropLabel(String.valueOf(crop.get("status"))));
                        Label comparison = new Label("Oferta estimada: " + crop.get("ofertaHidricaMm") + " mm  ·  Demanda de referência: " + crop.get("demandaMm") + " mm");
                        Label message = new Label(String.valueOf(crop.get("mensagem")));
                        Label source = new Label("Fonte: " + crop.get("fonte"));
                        comparison.getStyleClass().add("body-copy");
                        message.getStyleClass().add("muted");
                        source.getStyleClass().add("source-note");
                        cropCard.getChildren().addAll(comparison, message, source);
                        content.getChildren().add(cropCard);
                    }
                }
            }
            root.setCenter(wrap(content));
            showFeedback("Cálculo consultivo — decisão final sempre do produtor.");
        } catch (RuntimeException error) {
            showError("Não foi possível calcular a autonomia: " + error.getMessage());
        }
    }

    private void showReservoirs() {
        try {
            content = new VBox(18);
            content.setPadding(new Insets(28));
            Label title = new Label("Reservatórios");
            title.getStyleClass().add("page-title");
            Label description = new Label("Níveis exibidos apenas para sua propriedade. O monitor comunitário apresenta informações agregadas.");
            description.getStyleClass().add("muted");
            content.getChildren().addAll(title, description);

            for (Reservatorio reservoir : reservatorioService.listarPorLocalidade(localidadeId)) {
                VBox card = sectionCard(reservoir.getNome(), reservoir.getTipo());
                double level = reservoir.getNivelPercentual() == null ? 0 : reservoir.getNivelPercentual();
                ProgressBar progress = new ProgressBar(Math.max(0, Math.min(1, level / 100)));
                progress.setMaxWidth(Double.MAX_VALUE);
                Label levelLabel = new Label(String.format("%.0f%% de nível registrado", level));
                levelLabel.getStyleClass().add("body-copy");
                Label capacity = new Label("Capacidade: " + value(reservoir.getCapacidadeLitros()) + " L · última atualização: " + formatInstant(reservoir.getUltimaSincronizacao()));
                capacity.getStyleClass().add("muted");
                card.getChildren().addAll(levelLabel, progress, capacity);
                content.getChildren().add(card);
            }
            root.setCenter(wrap(content));
            showFeedback("Os dados comunitários não expõem reservatórios individuais.");
        } catch (RuntimeException error) {
            showError("Não foi possível carregar os reservatórios: " + error.getMessage());
        }
    }

    private void showCommunity() {
        try {
            content = new VBox(18);
            content.setPadding(new Insets(28));
            Label title = new Label("Comunidade");
            title.getStyleClass().add("page-title");
            Map<String, Object> monitor = comunidadService.monitorLocalidade(localidadeId);
            HBox summary = new HBox(16,
                    metricCard("AVISOS ATIVOS", String.valueOf(monitor.get("totalAvisos")), "Mural local", "ochre"),
                    metricCard("PARTICIPANTES", String.valueOf(monitor.get("participantes")), "Resumo agregado", "olive")
            );
            summary.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));
            content.getChildren().addAll(title, summary);

            VBox form = sectionCard("Publicar aviso", "Compartilhe um recado ou oferta com a localidade");
            TextField subject = new TextField();
            subject.setPromptText("Título");
            TextArea message = new TextArea();
            message.setPromptText("Escreva sua mensagem");
            message.setPrefRowCount(3);
            Button publish = new Button("Publicar aviso");
            publish.getStyleClass().add("button-primary");
            publish.setOnAction(event -> {
                try {
                    AvisoComunitario notice = new AvisoComunitario(null, localidadeId,
                            subject.getText().trim(), message.getText().trim(), "Agricultor");
                    comunidadService.publicar(notice);
                    showFeedback("Aviso publicado.");
                    showCommunity();
                } catch (RuntimeException error) {
                    showError("Não foi possível publicar o aviso: " + error.getMessage());
                }
            });
            form.getChildren().addAll(subject, message, publish);
            content.getChildren().add(form);

            for (AvisoComunitario notice : comunidadService.listar(localidadeId)) {
                VBox card = sectionCard(notice.getTitulo(), formatInstant(notice.getCriadoEm()));
                Label body = new Label(notice.getMensagem());
                body.setWrapText(true);
                body.getStyleClass().add("body-copy");
                card.getChildren().add(body);
                content.getChildren().add(card);
            }
            root.setCenter(wrap(content));
            showFeedback("Visão comunitária agregada · última sincronização " + now());
        } catch (RuntimeException error) {
            showError("Não foi possível carregar o mural: " + error.getMessage());
        }
    }

    private VBox metricCard(String title, String value, String detail, String colorClass) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("metric-card", colorClass);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");
        Label detailLabel = new Label(detail);
        detailLabel.getStyleClass().add("muted");
        card.getChildren().addAll(titleLabel, valueLabel, detailLabel);
        return card;
    }

    private VBox sectionCard(String title, String subtitle) {
        VBox card = new VBox(12);
        card.getStyleClass().add("surface-card");
        Label heading = new Label(title);
        heading.getStyleClass().add("section-title");
        Label detail = new Label(subtitle);
        detail.getStyleClass().add("muted");
        card.getChildren().addAll(heading, detail);
        return card;
    }

    private Usuario requireDemoUser() {
        Usuario user = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Usuário de demonstração não encontrado."));
        if (user.getPerfilProdutivo() == null) {
            throw new IllegalStateException("Cadastre o perfil produtivo da propriedade para calcular a autonomia.");
        }
        return user;
    }

    private void showError(String message) {
        feedback.setText(message);
        if (!feedback.getStyleClass().contains("error-text")) {
            feedback.getStyleClass().add("error-text");
        }
    }

    private void showFeedback(String message) {
        feedback.setText(message);
        feedback.getStyleClass().remove("error-text");
    }

    private static String value(Object value) {
        return value == null ? "—" : String.valueOf(value);
    }

    private static String weatherTitle(String status) {
        return switch (status.toLowerCase()) {
            case "normal" -> "Chuva dentro da média observada";
            case "atencao" -> "Chuva abaixo do normal para o período";
            case "alerta" -> "Pouca chuva registrada até agora";
            default -> "Dados de chuva disponíveis";
        };
    }

    private static String cropLabel(String status) {
        return switch (status) {
            case "VIAVEL" -> "Viável";
            case "VIAVEL_COM_RESTRICAO" -> "Viável com restrição";
            default -> "Inviável nas condições atuais";
        };
    }

    private static String statusClass(String status) {
        return switch (status) {
            case "VIAVEL" -> "badge-good";
            case "VIAVEL_COM_RESTRICAO" -> "badge-warning";
            default -> "badge-risk";
        };
    }

    private static String now() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(java.time.Instant.now());
    }

    private static String formatInstant(java.time.Instant instant) {
        return instant == null ? "sem data" : DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault()).format(instant);
    }
}
