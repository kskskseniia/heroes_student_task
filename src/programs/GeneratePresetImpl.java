package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    // Ограничения по условиям
    private static final int LIMIT_PER_TYPE = 11;

    // Поле для армии компьютера
    private static final int FIELD_WIDTH = 3;
    private static final int FIELD_HEIGHT = 21;

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Итоговая армия компьютера
        List<Unit> armyUnits = new ArrayList<>();
        int usedPoints = 0;

        // На всякий случай обрабатываем некорректный ввод
        if (unitList == null || unitList.isEmpty() || maxPoints <= 0) {
            Army emptyArmy = new Army(Collections.emptyList());
            emptyArmy.setPoints(0);
            return emptyArmy;
        }

        // 1) Сортируем "типы" юнитов по эффективности:
        //    - сначала по (атака/стоимость) по убыванию,
        //    - при равенстве — по (здоровье/стоимость) по убыванию,
        //    - если и это совпало — по атаке по убыванию.
        List<Unit> types = new ArrayList<>(unitList);
        types.sort(Comparator
                .comparingDouble((Unit u) -> (double) u.getBaseAttack() / u.getCost())
                .thenComparingDouble(u -> (double) u.getHealth() / u.getCost())
                .thenComparingInt(Unit::getBaseAttack)
                .reversed());
        // 2) Чтобы юниты не накладывались друг на друга, помечаем занятые клетки
        //    Ключ формируем как "x,y".
        Set<String> busyCells = new HashSet<>();
        Random rng = new Random();

        // 3) Жадно набираем армию: идём по типам в порядке убывания эффективности
        for (Unit proto : types) {
            if (proto == null) continue;

            int unitsCount  = 0;

            // Добавляем максимум юнитов данного типа, пока хватает очков
            while (unitsCount  < LIMIT_PER_TYPE && usedPoints + proto.getCost() <= maxPoints) {
                int x;
                int y;
                String key;

                // Находим свободную клетку на своей половине
                do {
                    x = rng.nextInt(FIELD_WIDTH);
                    y = rng.nextInt(FIELD_HEIGHT);
                    key = x + "," + y;
                } while (busyCells.contains(key));

                busyCells.add(key);

                // Имя формате "UnitType N"
                Unit unit = new Unit(
                        proto.getUnitType() + " " + (unitsCount  + 1),
                        proto.getUnitType(),
                        proto.getHealth(),
                        proto.getBaseAttack(),
                        proto.getCost(),
                        proto.getAttackType(),
                        proto.getAttackBonuses(),
                        proto.getDefenceBonuses(),
                        x,
                        y
                );

                armyUnits.add(unit);
                usedPoints += unit.getCost();
                unitsCount ++;
            }
        }

        Army computerArmy = new Army(armyUnits);
        computerArmy.setPoints(usedPoints);
        return computerArmy;
    }
}