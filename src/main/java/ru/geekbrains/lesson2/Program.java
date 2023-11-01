package ru.geekbrains.lesson2;

import java.util.Random;
import java.util.Scanner;

public class Program {

    private static final char DOT_HUMAN = 'X'; // Фишка игрока - человека
    private static final char DOT_AI = '0'; // Фишка игрока - компьютер
    private static final char DOT_EMPTY = '*'; // Признак пустого поля
    private static final Scanner scanner = new Scanner(System.in);
    private static  final Random random = new Random();
    private static char[][] field; // Двумерный массив хранит состояние игрового поля
    private static int fieldSizeX; // Размерность игрового поля
    private static int fieldSizeY; // Размерность игрового поля
    private static final int WIN_COUNT = 4; // Кол-во фишек для победы

    public static void main(String[] args) {
        while (true){
            initialize();
            printField();
            while (true){
                humanTurn();
                printField();
                if (gameCheck(DOT_HUMAN, "Вы победили!"))
                    break;
                aiTurn();
                printField();
                if (gameCheck(DOT_AI, "Победил компьютер!"))
                    break;
            }
            System.out.print("Желаете сыграть еще раз? (Y - да): ");
            if (!scanner.next().equalsIgnoreCase("Y"))
                break;
        }
    }

    /**
     * Инициализация начального состояния игры
     */
    private static void initialize(){
        fieldSizeX = 5;
        fieldSizeY = 5;
        field = new char[fieldSizeY][fieldSizeX];
        for (int y = 0; y < fieldSizeY; y++){
            for (int x = 0; x < fieldSizeX; x++){
                field[y][x] = DOT_EMPTY;
            }
        }
    }

    /**
     * Отрисовать текущее состояние игрового поля
     */
    private static void printField(){
        System.out.print("+");
        for (int i = 0; i < fieldSizeX*2 + 1; i++){
            System.out.print((i % 2 == 0) ? "-" : i / 2 + 1);
        }
        System.out.println();

        for (int i = 0; i < fieldSizeY; i++){
            System.out.print(i + 1 + "|");
            for (int j = 0; j < fieldSizeX; j++){
                System.out.print(field[i][j] + "|");
            }
            System.out.println();
        }

        for (int i = 0; i < fieldSizeX*2 + 2; i++){
            System.out.print("-");
        }
        System.out.println();
    }

    /**
     * Обработка хода игрока (человека)
     */
    private static void humanTurn(){
        int x, y;
        do{
            System.out.print("Укажите координаты хода X и Y (от 1 до 3)\nчерез пробел: ");
            x = scanner.nextInt() - 1;
            y = scanner.nextInt() - 1;
        }
        while (!isCellValid(x, y) || !isCellEmpty(x, y));
        field[x][y] = DOT_HUMAN;
    }

    /**
     * Обработка хода компьютера
     */
    static void aiTurn(){
        int x, y;
        int[] move = aiFindMove(DOT_AI);
        x = move[0];
        y = move[1];
        field[x][y] = DOT_AI;
    }

    //наделим интеллектом бота
    private static int[] aiFindMove(char dot) {
        int[] move = new int[2];
        //тут ход на победу
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isCellEmpty(i, j)) {
                    field[i][j] = dot;
                    if (checkWin(dot)) {
                        move[0] = i;
                        move[1] = j;
                        return move;
                    }
                    field[i][j] = DOT_EMPTY;
                }
            }
        }
        //тут ход на защиту
        char opponentDot;
        if (dot == DOT_AI) {
            opponentDot = DOT_HUMAN;
        } else {
            opponentDot = DOT_AI;
        }
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isCellEmpty(i, j)) {
                    field[i][j] = opponentDot;
                    if (checkWin(opponentDot)) {
                        move[0] = i;
                        move[1] = j;
                        return move;
                    }
                    field[i][j] = DOT_EMPTY;
                }
            }
        }
        //если есть место для хода и нет смысла блокировать/защищаться
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isCellEmpty(i, j)) {
                    move[0] = i;
                    move[1] = j;
                    return move;
                }
            }
        }
        return move;
    }

    /**
     * Проверка, ячейка является пустой (DOT_EMPTY)
     * @param x
     * @param y
     * @return
     */
    static boolean isCellEmpty(int x, int y){
        return field[x][y] == DOT_EMPTY;
    }

    /**
     * Проверка состояния игры
     * @param dot фишка игрока
     * @param winStr победный слоган
     * @return признак продолжения игры (true - завершение игры)
     */
    static boolean gameCheck(char dot, String winStr){
        if (checkWin(dot)){
            System.out.println(winStr);
            return true;
        }
        if (checkDraw()){
            System.out.println("Ничья!");
            return true;
        }
        return false; // Продолжим игру
    }

    /**
     * Проверка корректности ввода
     * @param x
     * @param y
     * @return
     */
    static boolean isCellValid(int x, int y){
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    /**
     * Проверка победы
     * @param c фишка игрока (X или 0)
     * @return
     */
    static boolean checkWin(char c){
    //самое простое это сперва пустить цикл на проверку горизонталей и вертикалей
        // определить счётчики рядов и столбцов:
        // если один из счётчиков превышает 4, то победа
        int rowCounter = 0;
        int colCounter = 0;
        //горизонтали
        for (int i = 0; i < fieldSizeY; i++){
            for (int j = 0; j < fieldSizeX; j++){
                if (field[i][j] == c) rowCounter++;
                if (field[j][i] == c) colCounter++;
            }
            if (rowCounter == WIN_COUNT || colCounter == WIN_COUNT) return true;
            rowCounter = 0;
            colCounter = 0;
        }
        //диагонали
        for (int i = 0; i <= fieldSizeY - WIN_COUNT; i++) {
            for (int j = 0; j <= fieldSizeX - WIN_COUNT; j++) {
                if (checkDiagonal(i, j, 1, 1, c)
                        || checkDiagonal(i, j + WIN_COUNT - 1, 1, -1, c)) {
                    return true;
                }
            }
        }


        return false;
    }

    //метод проверки диагоналей чуть подсмотрел, каюсь...
    static boolean checkDiagonal(int startY, int startX, int deltaY, int deltaX, char c) {
        int count = 0;
        for (int i = 0; i < WIN_COUNT; i++) {
            if (field[startY + i * deltaY][startX + i * deltaX] == c) { //долго тупил с условием
                count++;
            }
            if (count == WIN_COUNT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка на ничью
     * @return
     */
    static boolean checkDraw(){
        for (int i = 0; i < fieldSizeY; i++){
            for (int j = 0; j < fieldSizeX; j++){
                if (isCellEmpty(i, j)) return false;
            }
        }
        return true;
    }

}
