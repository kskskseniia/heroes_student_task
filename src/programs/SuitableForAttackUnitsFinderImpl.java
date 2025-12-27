package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        if (unitsByRow == null || unitsByRow.isEmpty()) {
            return suitableUnits;
        }

        // unitsByRow: список "строк" противника .
        // Внутри строки выбираем "крайнего" по X:
        // - если атакуем левую армию (isLeftArmyTarget=true)  -> берём минимальный x
        // - если атакуем правую армию (isLeftArmyTarget=false) -> берём максимальный x
        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) continue;

            Unit best = null;

            for (Unit u : row) {
                if (u == null || !u.isAlive()) continue;

                if (best == null) {
                    best = u;
                    continue;
                }

                if (isLeftArmyTarget) {
                    if (u.getxCoordinate() < best.getxCoordinate()) {
                        best = u;
                    }
                } else {
                    if (u.getxCoordinate() > best.getxCoordinate()) {
                        best = u;
                    }
                }
            }

            if (best != null) {
                suitableUnits.add(best);
            }
        }

        return suitableUnits;
    }
}
