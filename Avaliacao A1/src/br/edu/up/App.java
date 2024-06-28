package br.edu.up;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.edu.up.modelos.Aluno;

public class App {
    public static void main(String[] args) throws FileNotFoundException {

        String alunosFile = "src/alunos.csv";
        String resumoFile = "resumo.csv";

        List<Aluno> alunos = new ArrayList<>();

        Scanner alunosScanner = new Scanner(new File(alunosFile));
        if (alunosScanner.hasNextLine()) {
            String header = alunosScanner.nextLine();
            if (!header.contains("Matrícula") && !header.contains("Nome") && !header.contains("Nota")) {
                alunosScanner.close();
                alunosScanner = new Scanner(new File(alunosFile));
            }
        }

        while (alunosScanner.hasNextLine()) {
            String line = alunosScanner.nextLine();
            String[] data = line.split(";");
            if (data.length == 3) {
                try {
                    int matricula = Integer.parseInt(data[0]);
                    String nome = data[1];
                    double nota = Double.parseDouble(data[2].replace(',', '.'));
                    Aluno aluno = new Aluno(matricula, nome, nota);
                    alunos.add(aluno);
                } catch (NumberFormatException e) {
                    System.out.println("Erro ao converter dados: " + line);
                }
            } else {
                System.out.println("Linha ignorada: " + line);
            }
        }
        alunosScanner.close();

        int totalAlunos = alunos.size();
        long aprovados = alunos.stream().filter(a -> a.getNota() >= 6.0).count();
        long reprovados = totalAlunos - aprovados;
        double menorNota = alunos.stream().mapToDouble(Aluno::getNota).min().orElse(0);
        double maiorNota = alunos.stream().mapToDouble(Aluno::getNota).max().orElse(0);
        double mediaNotas = BigDecimal.valueOf(alunos.stream().mapToDouble(Aluno::getNota).average().orElse(0)).setScale(2, RoundingMode.HALF_UP).doubleValue();

        try (PrintWriter writer = new PrintWriter(resumoFile)) {
            writer.println("Quantidade de alunos na turma;" + totalAlunos);
            writer.println("Quantidade de aprovados;" + aprovados);
            writer.println("Quantidade de reprovados;" + reprovados);
            writer.println("Menor nota;" + menorNota);
            writer.println("Maior nota;" + maiorNota);
            writer.println("Média geral;" + mediaNotas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
