package com.manejocaatingueiro.ui;

import com.manejocaatingueiro.ManejoCaatingueiroApplication;
import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.model.Localidade;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.model.StatusSafra;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.service.ClimaService;
import com.manejocaatingueiro.service.ComunidadService;
import com.manejocaatingueiro.service.PlantioService;
import com.manejocaatingueiro.service.ReservatorioService;
import com.manejocaatingueiro.service.UsuarioService;
import com.manejocaatingueiro.service.RegistroSafraService;
import com.manejocaatingueiro.service.SolicitacaoAguaService;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.CheckBox;
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
import java.time.LocalDate;
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
    private RegistroSafraService registroSafraService;
    private SolicitacaoAguaService solicitacaoAguaService;
    private BorderPane root;
    private VBox content;
    private Label feedback;
    private ComboBox<Localidade> localidadeCombo;
    private Button activeNavButton;
    private int localidadeId = 1;
    private Usuario usuarioAtual;
    private Stage stageAtual;

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
        registroSafraService = springContext.getBean(RegistroSafraService.class);
        solicitacaoAguaService = springContext.getBean(SolicitacaoAguaService.class);

        root = new BorderPane();
        root.getStyleClass().add("app-root");
        stageAtual = stage;
        content = new VBox(20);
        content.setPadding(new Insets(28));
        root.setCenter(wrap(content));

        Scene scene = new Scene(root, 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        stage.setTitle("Manejo Caatingueiro");
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
        showLogin();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    private void showLogin() {
        root.setLeft(null);
        root.setTop(null);
        root.setBottom(null);
        VBox form = new VBox(14);
        form.setMaxWidth(480);
        form.setPadding(new Insets(36));
        form.getStyleClass().add("surface-card");
        form.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Acesse sua propriedade");
        title.getStyleClass().add("page-title");
        TextField email = new TextField();
        email.setPromptText("E-mail");
        PasswordField password = new PasswordField();
        password.setPromptText("Senha");
        Label status = new Label();
        status.getStyleClass().add("error-text");
        Button enter = new Button("Entrar");
        enter.getStyleClass().add("button-primary");
        enter.setOnAction(event -> {
            try {
                usuarioAtual = usuarioService.autenticar(email.getText(), password.getText());
                localidadeId = usuarioAtual.getLocalidadeId();
                abrirPainel();
            } catch (RuntimeException error) {
                status.setText("Não foi possível entrar: " + error.getMessage());
            }
        });
        Button register = new Button("Criar cadastro");
        register.getStyleClass().add("button-secondary");
        register.setOnAction(event -> showRegistration());
        form.getChildren().addAll(title, email, password, enter, register, status);
        root.setCenter(wrap(form));
    }

    private void showRegistration() {
        VBox form = new VBox(12);
        form.setMaxWidth(560);
        form.setPadding(new Insets(30));
        form.getStyleClass().add("surface-card");
        Label title = new Label("Cadastro e perfil produtivo");
        title.getStyleClass().add("page-title");
        TextField name = new TextField();
        name.setPromptText("Nome");
        TextField email = new TextField();
        email.setPromptText("E-mail");
        TextField phone = new TextField();
        phone.setPromptText("Telefone/WhatsApp");
        PasswordField password = new PasswordField();
        password.setPromptText("Senha");
        ComboBox<Localidade> locality = new ComboBox<>(
                FXCollections.observableArrayList(climaService.listarLocalidades()));
        locality.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Localidade value) {
                return value == null ? "" : value.getNome();
            }
            @Override
            public Localidade fromString(String value) { return null; }
        });
        locality.getSelectionModel().selectFirst();
        ComboBox<com.manejocaatingueiro.model.FonteAgua> waterSource =
                new ComboBox<>(FXCollections.observableArrayList(com.manejocaatingueiro.model.FonteAgua.values()));
        waterSource.getSelectionModel().select(com.manejocaatingueiro.model.FonteAgua.CISTERNA);
        TextField people = numberField("Pessoas", "4");
        TextField cattle = numberField("Bovinos", "0");
        TextField goats = numberField("Caprinos/ovinos", "0");
        TextField birds = numberField("Aves", "0");
        TextField irrigatedArea = numberField("Área irrigada (m²)", "0");
        TextField horses = numberField("Cavalos/jumentos", "0");
        TextField pigs = numberField("Suínos", "0");
        Label status = new Label();
        status.getStyleClass().add("error-text");
        Button save = new Button("Criar conta");
        save.getStyleClass().add("button-primary");
        save.setOnAction(event -> {
            try {
                Usuario novo = new Usuario();
                novo.setNome(name.getText());
                novo.setEmail(email.getText());
                novo.setTelefone(phone.getText());
                novo.setSenhaHash(password.getText());
                novo.setAbastecimento(waterSource.getValue());
                novo.setLocalidadeId(locality.getValue().getId());
                novo.setPerfilProdutivo(new com.manejocaatingueiro.model.PerfilProdutivo(
                        parseInteger(people), parseInteger(cattle), parseInteger(goats),
                        parseInteger(birds), parseInteger(irrigatedArea)));
                novo.getPerfilProdutivo().setCavalosJumentos(parseInteger(horses));
                novo.getPerfilProdutivo().setSuinos(parseInteger(pigs));
                usuarioAtual = usuarioService.registrar(novo);
                localidadeId = usuarioAtual.getLocalidadeId();
                abrirPainel();
            } catch (RuntimeException error) {
                status.setText("Não foi possível cadastrar: " + error.getMessage());
            }
        });
        Button back = new Button("Voltar ao login");
        back.setOnAction(event -> showLogin());
        form.getChildren().addAll(title, name, email, phone, password, locality, waterSource,
                people, cattle, goats, birds, horses, pigs, irrigatedArea, save, back, status);
        root.setLeft(null);
        root.setTop(null);
        root.setBottom(null);
        root.setCenter(wrap(form));
    }

    private void abrirPainel() {
        root.setLeft(buildSidebar());
        root.setTop(buildHeader());
        root.setBottom(buildFooter());
        stageAtual.setTitle("Manejo Caatingueiro — " + usuarioAtual.getNome());
        showDashboard();
    }

    private TextField numberField(String label, String defaultValue) {
        TextField field = new TextField(defaultValue);
        field.setPromptText(label);
        return field;
    }

    private int parseInteger(TextField field) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Informe quantidades inteiras válidas no perfil produtivo.");
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
                navButton("Comunidade", this::showCommunity, false),
                navButton("Safras e cenários", this::showHarvests, false),
                navButton("Solicitações", this::showRequests, false)
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
                new Label("Bem-vindo(a), " + (usuarioAtual == null ? "produtor(a)" : usuarioAtual.getNome())),
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
            Usuario user = requireCurrentUser();
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
                    metricCard("AUTONOMIA HÍDRICA", water.get("autonomiaDias") == null
                                    ? "Indeterminada" : value(water.get("autonomiaDias")) + " dias",
                            "Status: " + value(water.get("statusAutonomia")), "olive"),
                    metricCard("SEUS RESERVATÓRIOS", String.valueOf(reservatorioService.listarPorUsuario(user.getId()).size()),
                            "Dados privados da propriedade", "ochre")
            );
            metrics.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));

            HBox details = new HBox(16, buildRainChart(climate), buildCropSummary(water));
            HBox.setHgrow(details.getChildren().get(0), Priority.ALWAYS);
            HBox.setHgrow(details.getChildren().get(1), Priority.ALWAYS);
            content.getChildren().addAll(metrics, details);
            root.setCenter(wrap(content));
            showFeedback("Fonte: " + value(climate.get("fonte")) + " · Atualizado " + now()
                    + (Boolean.TRUE.equals(climate.get("dadosInsuficientes")) ? " · Dados insuficientes" : ""));
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
            Map<String, Object> result = plantioService.avaliarAptidao(localidadeId, requireCurrentUser());
            content = new VBox(18);
            content.setPadding(new Insets(28));
            Label title = new Label("Plantio e autonomia hídrica");
            title.getStyleClass().add("page-title");
            Label description = new Label("Estimativa consultiva com base no consumo cadastrado e na disponibilidade hídrica.");
            description.getStyleClass().add("muted");
            content.getChildren().addAll(title, description);
            content.getChildren().add(buildPrecipitationForm());
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

            for (Reservatorio reservoir : reservatorioService.listarPorUsuario(requireCurrentUser().getId())) {
                VBox card = sectionCard(reservoir.getNome(), reservoir.getTipo());
                double level = reservoir.getNivelPercentual() == null ? 0 : reservoir.getNivelPercentual();
                ProgressBar progress = new ProgressBar(Math.max(0, Math.min(1, level / 100)));
                progress.setMaxWidth(Double.MAX_VALUE);
                Label levelLabel = new Label(String.format("%.0f%% de nível registrado", level));
                levelLabel.getStyleClass().add("body-copy");
                Label capacity = new Label("Capacidade: " + value(reservoir.getCapacidadeLitros()) + " L · última atualização: " + formatInstant(reservoir.getUltimaSincronizacao()));
                capacity.getStyleClass().add("muted");
                TextField newLevel = new TextField();
                newLevel.setPromptText("Novo nível em %");
                TextField checklistItem = new TextField();
                checklistItem.setPromptText("Item do checklist sanitário");
                Button saveLevel = new Button("Registrar nível");
                saveLevel.setOnAction(event -> {
                    try {
                        reservatorioService.atualizarNivel(reservoir.getId(), usuarioAtual.getId(),
                                Double.parseDouble(newLevel.getText().replace(',', '.')));
                        showFeedback("Nível atualizado no histórico do reservatório.");
                        showReservoirs();
                    } catch (RuntimeException error) {
                        showError("Não foi possível registrar o nível: " + error.getMessage());
                    }
                });
                Button saveChecklist = new Button("Registrar checklist");
                saveChecklist.setOnAction(event -> {
                    try {
                        reservatorioService.registrarChecklist(reservoir.getId(), usuarioAtual.getId(),
                                checklistItem.getText(), true);
                        showFeedback("Checklist sanitário registrado.");
                        showReservoirs();
                    } catch (RuntimeException error) {
                        showError("Não foi possível registrar o checklist: " + error.getMessage());
                    }
                });
                card.getChildren().addAll(levelLabel, progress, capacity, newLevel, saveLevel,
                        checklistItem, saveChecklist);
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
            for (var alert : springContext.getBean(com.manejocaatingueiro.service.AlertaService.class)
                    .consultarAtivos(localidadeId)) {
                VBox alertCard = sectionCard(alert.getTipo(), "Severidade: " + alert.getSeveridade());
                Label message = new Label(alert.getMensagem());
                message.setWrapText(true);
                alertCard.getChildren().add(message);
                content.getChildren().add(alertCard);
            }

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
                            subject.getText().trim(), message.getText().trim(), usuarioAtual.getNome());
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

    private Node buildPrecipitationForm() {
        VBox form = sectionCard("Registrar chuva", "Anote o volume observado para complementar os dados externos");
        TextField volume = new TextField();
        volume.setPromptText("Volume (mm)");
        DatePicker date = new DatePicker(LocalDate.now());
        ComboBox<String> source = new ComboBox<>(FXCollections.observableArrayList(
                "PLUVIOMETRO_PROPRIO", "ESTACAO", "ESTIMATIVA"));
        source.getSelectionModel().selectFirst();
        Button save = new Button("Salvar registro");
        save.getStyleClass().add("button-primary");
        save.setOnAction(event -> {
            try {
                climaService.registrarPrecipitacao(localidadeId,
                        Double.parseDouble(volume.getText().replace(',', '.')),
                        source.getValue(), date.getValue());
                showFeedback("Registro de chuva salvo localmente.");
                showPlanting();
            } catch (RuntimeException error) {
                showError("Não foi possível registrar a chuva: " + error.getMessage());
            }
        });
        form.getChildren().addAll(volume, date, source, save);
        return form;
    }

    private void showHarvests() {
        try {
            Usuario user = requireCurrentUser();
            content = new VBox(18);
            content.setPadding(new Insets(28));
            Label title = new Label("Safras e cenários");
            title.getStyleClass().add("page-title");
            content.getChildren().add(title);

            VBox simulate = sectionCard("Simular déficit hídrico", "Compare hipóteses sem alterar os dados cadastrados");
            TextField rainFactor = numberField("Fator de chuva (0 a 2)", "0.7");
            TextField extraCattle = numberField("Bovinos adicionais", "0");
            Button simulateButton = new Button("Calcular cenário");
            Label simulationResult = new Label();
            simulationResult.setWrapText(true);
            simulateButton.setOnAction(event -> {
                try {
                    Map<String, Object> result = plantioService.simularCenario(user, LocalDate.now(),
                            Double.parseDouble(rainFactor.getText().replace(',', '.')),
                            0, parseInteger(extraCattle), 0, 0, 0, 0);
                    simulationResult.setText("Autonomia: " + value(result.get("autonomiaDias"))
                            + " dias · Status: " + value(result.get("statusAutonomia"))
                            + " · " + result.get("aviso"));
                } catch (RuntimeException error) {
                    showError("Não foi possível simular o cenário: " + error.getMessage());
                }
            });
            simulate.getChildren().addAll(rainFactor, extraCattle, simulateButton, simulationResult);
            content.getChildren().add(simulate);

            VBox newHarvest = sectionCard("Registrar plantio", "Inicie o histórico da cultura na sua propriedade");
            ComboBox<Cultura> crop = new ComboBox<>(FXCollections.observableArrayList(plantioService.listarCulturas()));
            crop.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Cultura value) { return value == null ? "" : value.getNome(); }
                @Override
                public Cultura fromString(String value) { return null; }
            });
            crop.getSelectionModel().selectFirst();
            DatePicker plantingDate = new DatePicker(LocalDate.now());
            TextField note = new TextField();
            note.setPromptText("Observação opcional");
            Button create = new Button("Salvar plantio");
            create.setOnAction(event -> {
                try {
                    registroSafraService.iniciar(user, crop.getValue().getId(),
                            plantingDate.getValue(), note.getText());
                    showFeedback("Plantio registrado no histórico.");
                    showHarvests();
                } catch (RuntimeException error) {
                    showError("Não foi possível registrar a safra: " + error.getMessage());
                }
            });
            newHarvest.getChildren().addAll(crop, plantingDate, note, create);
            content.getChildren().add(newHarvest);

            for (var harvest : registroSafraService.listar(user.getId())) {
                VBox card = sectionCard(harvest.getCulturaNome(),
                        "Plantio em " + harvest.getDataPlantio() + " · " + harvest.getResultado());
                HBox actions = new HBox(10);
                Button evaluateRisk = new Button("Avaliar risco");
                evaluateRisk.setOnAction(event -> {
                    try {
                        Map<String, Object> risk = registroSafraService.avaliarRisco(user.getId(), harvest.getId());
                        showFeedback(Boolean.TRUE.equals(risk.get("alertaEmitido"))
                                ? "Alerta de risco emitido." : "Avaliação atualizada; confira os dados disponíveis.");
                        showHarvests();
                    } catch (RuntimeException error) {
                        showError("Não foi possível avaliar o risco: " + error.getMessage());
                    }
                });
                ComboBox<StatusSafra> outcome = new ComboBox<>(FXCollections.observableArrayList(
                        StatusSafra.SUCESSO, StatusSafra.PERDA_PARCIAL, StatusSafra.PERDA_TOTAL));
                outcome.getSelectionModel().selectFirst();
                Button finish = new Button("Registrar resultado");
                finish.setOnAction(event -> {
                    try {
                        registroSafraService.registrarResultado(user.getId(), harvest.getId(),
                                outcome.getValue(), null);
                        showFeedback("Resultado da safra registrado.");
                        showHarvests();
                    } catch (RuntimeException error) {
                        showError("Não foi possível registrar o resultado: " + error.getMessage());
                    }
                });
                actions.getChildren().addAll(evaluateRisk, outcome, finish);
                card.getChildren().add(actions);
                content.getChildren().add(card);
            }
            root.setCenter(wrap(content));
            showFeedback("Histórico e simulação são consultivos; nenhum cenário altera seu cadastro.");
        } catch (RuntimeException error) {
            showError("Não foi possível carregar safras e cenários: " + error.getMessage());
        }
    }

    private void showRequests() {
        try {
            Usuario user = requireCurrentUser();
            content = new VBox(18);
            content.setPadding(new Insets(28));
            Label title = new Label("Solicitações de água");
            title.getStyleClass().add("page-title");
            Label gate = new Label("Canal desativado até validação do piloto e pactuação institucional (RN14).");
            gate.getStyleClass().add("muted");
            content.getChildren().addAll(title, gate);
            for (var request : solicitacaoAguaService.listar(user, null, false)) {
                content.getChildren().add(sectionCard("Solicitação " + request.getId(),
                        request.getStatus() + " · " + request.getMotivo()));
            }
            root.setCenter(wrap(content));
        } catch (RuntimeException error) {
            showError("Não foi possível carregar as solicitações: " + error.getMessage());
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

    private Usuario requireCurrentUser() {
        Usuario user = usuarioAtual;
        if (user == null) {
            throw new IllegalStateException("Faça login para acessar os dados da propriedade.");
        }
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
            case "dados_insuficientes" -> "Ainda faltam dados climáticos confiáveis";
            default -> "Dados de chuva disponíveis";
        };
    }

    private static String cropLabel(String status) {
        return switch (status) {
            case "VIAVEL" -> "Viável";
            case "VIAVEL_COM_RESTRICAO" -> "Viável com restrição";
            case "DADOS_INSUFICIENTES" -> "Dados insuficientes";
            default -> "Inviável nas condições atuais";
        };
    }

    private static String statusClass(String status) {
        return switch (status) {
            case "VIAVEL" -> "badge-good";
            case "VIAVEL_COM_RESTRICAO" -> "badge-warning";
            case "DADOS_INSUFICIENTES" -> "badge-warning";
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
