package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        if (playerArmy == null || computerArmy == null) return;

        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();

        // Бой продолжается, пока в обеих армиях есть живые юниты
        while (hasAlive(playerUnits) && hasAlive(computerUnits)) {

            // 1) На начале раунда формируем очереди хода (живые + сортировка по атаке)
            List<Unit> playerQueue = getSortedAliveUnits(playerUnits);
            List<Unit> computerQueue = getSortedAliveUnits(computerUnits);

            int pIndex = 0;
            int cIndex = 0;

            // Если за весь раунд никто не смог атаковать (все цели == null),
            // значит бой "застрял" и его нужно завершить
            boolean anySuccessfulAttack = false;

            // 2) Ходы в раунде: компьютер -> игрок -> компьютер -> игрок ...
            while (pIndex < playerQueue.size() || cIndex < computerQueue.size()) {

                // Ход компьютера
                if (cIndex < computerQueue.size()) {
                    Unit attacker = computerQueue.get(cIndex);
                    if (attacker.isAlive()) {
                        Unit target = attacker.getProgram().attack();

                        if (printBattleLog != null) {
                            printBattleLog.printBattleLog(attacker, target);
                        }

                        if (target != null) {
                            anySuccessfulAttack = true;
                        }
                    }
                    cIndex++;
                }

                // Ход игрока
                if (pIndex < playerQueue.size()) {
                    Unit attacker = playerQueue.get(pIndex);
                    if (attacker.isAlive()) {
                        Unit target = attacker.getProgram().attack();

                        if (printBattleLog != null) {
                            printBattleLog.printBattleLog(attacker, target);
                        }

                        if (target != null) {
                            anySuccessfulAttack = true;
                        }
                    }
                    pIndex++
                }
            }

            // 3) Если никто за раунд не смог атаковать — завершаем симуляцию
            if (!anySuccessfulAttack) {
                break;
            }
        }
    }

    /**
     * Возвращает список живых юнитов, отсортированный по убыванию атаки.
     * При равенстве атаки — по убыванию здоровья (чтобы порядок был стабильнее).
     */
    private List<Unit> getSortedAliveUnits(List<Unit> units) {
        List<Unit> alive = new ArrayList<>();
        for (Unit u : units) {
            if (u != null && u.isAlive()) {
                alive.add(u);
            }
        }

        alive.sort(Comparator
                .comparingInt(Unit::getBaseAttack).reversed()
                .thenComparing(Comparator.comparingInt(Unit::getHealth).reversed())
                .thenComparing(Unit::getName)
        );

        return alive;
    }

    /**
     * Проверка: есть ли хотя бы один живой юнит в списке.
     */
    private boolean hasAlive(List<Unit> units) {
        for (Unit u : units) {
            if (u != null && u.isAlive()) {
                return true;
            }
        }
        return false;
    }
}