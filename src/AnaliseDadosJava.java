import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AnaliseDadosJava {

        public static void main(String[] args) {
            String arquivoCSV = "C:\\Users\\rafae\\Downloads\\dados_processados (1).csv"; // Caminho do arquivo
            String linha;
            String separador = ",";  // O CSV usa vírgula como separador
            List<String[]> clientes = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(arquivoCSV))) {
                br.readLine(); // Ignorar cabeçalho

                while ((linha = br.readLine()) != null) {
                    String[] dados = linha.split(separador);
                    if (dados.length >= 11) { // Verifica se há colunas suficientes
                        clientes.add(dados);
                    }
                }

                System.out.println("Clientes carregados: " + clientes.size());
                if (clientes.isEmpty()) {
                    System.out.println("Nenhum cliente encontrado. Encerrando...");
                    return;
                }

                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.println("\nDigite o CPF do cliente que deseja buscar (ou 0 para listar todos, ou 'sair' para encerrar): ");
                    String entrada = scanner.nextLine().trim(); // Lê a entrada completa

                    if (entrada.equalsIgnoreCase("sair")) { // Se o usuário digitar 'sair', encerra o programa corretamente
                        System.out.println("Encerrando programa...");
                        scanner.close();
                        return;
                    }

                    // Remove caracteres não numéricos para verificar o CPF
                    String cpf = entrada.replaceAll("[^0-9]", "");
                    boolean encontrado = false;

                    if (cpf.equals("0")) { // Lista todos os clientes
                        System.out.println("\nClientes com Financiamento Aplicado:");
                        for (String[] cliente : clientes) {
                            processarCliente(cliente);
                        }
                    } else { // Busca pelo CPF específico na coluna correta (índice 3)
                        for (String[] cliente : clientes) {
                            String cpfCliente = cliente[3].trim().replaceAll("[^0-9]", "");
                            if (cpfCliente.equals(cpf)) {
                                System.out.println("Cliente encontrado: " + cliente[2]); // Nome na coluna 2
                                encontrado = true;
                                processarCliente(cliente);
                                break;
                            }
                        }

                        if (!encontrado) {
                            System.out.println("Cliente não localizado.");
                        }
                    }
                }

            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            }
        }

        // Método para processar as informações de um cliente
        private static void processarCliente(String[] cliente) {
            try {
                String nome = cliente[2].trim();
                String historicoPagamento = normalizarHistorico(cliente[9].trim()); // Normaliza o histórico
                String endividamentoStr = cliente[10].trim().replace("%", "");

                double endividamento;
                try {
                    endividamento = Double.parseDouble(endividamentoStr);
                } catch (NumberFormatException e) {
                    System.out.println("Erro ao converter endividamento. Pulando cliente: " + nome);
                    return;
                }

                // Verifica se o cliente está qualificado para financiamento
                if (historicoPagamento.equals("Ruim")) {
                    System.out.printf("Nome: %s | Histórico: %s | Endividamento: %.2f%% | Financiamento: Sem financiamento - Crédito negado pelo histórico\n",
                            nome, historicoPagamento, endividamento);
                    return;
                }

                if (endividamento > 50) {
                    System.out.printf("Nome: %s | Histórico: %s | Endividamento: %.2f%% | Financiamento: Sem financiamento - Crédito negado pelo alto endividamento\n",
                            nome, historicoPagamento, endividamento);
                    return;
                }

                String taxa = calcularTaxaFinanciamento(historicoPagamento);
                String tipoFinanciamento = determinarTipoFinanciamento(historicoPagamento);

                System.out.printf("Nome: %s | Histórico: %s | Endividamento: %.2f%% | Taxa: %s | Financiamento: %s\n",
                        nome, historicoPagamento, endividamento,
                        (taxa.equals("Sem financiamento") || taxa.equals("Desconhecido")) ? taxa : taxa + " %",
                        tipoFinanciamento);

            } catch (Exception e) {
                System.out.println("Erro ao processar cliente. Pulando...");
            }
        }

        // Método para normalizar o histórico de pagamento
        private static String normalizarHistorico(String historico) {
            // Remove espaços em branco nas extremidades e converte para minúsculas
            historico = historico.trim().toLowerCase();
            // Verifica o valor retorna um valor padronizado
            switch (historico) {
                case "excelente": return "Excelente";
                case "muito bom": return "Muito Bom";
                case "bom": return "Bom";
                case "regular": return "Regular";
                case "ruim":
                case "irregular":
                case "muito ruim":
                case "pessimo":
                case "péssimo": return "Ruim";
                default: return "Desconhecido";
            }
        }

        // Método para calcular a taxa de financiamento
        private static String calcularTaxaFinanciamento(String historico) {
            switch (historico) {
                case "Excelente": return "10";
                case "Muito Bom": return "20";
                case "Bom": return "30";
                case "Regular": return "40";
                case "Ruim": return "Sem financiamento";
                default: return "Desconhecido";
            }
        }

        // Método para determinar o tipo de financiamento
        private static String determinarTipoFinanciamento(String historico) {
            switch (historico) {
                case "Excelente":
                case "Muito Bom":
                    return "Tabela Price";
                case "Bom":
                case "Regular":
                    return "Tabela SAC";
                default:
                    return "Sem financiamento";
            }
        }
    }




