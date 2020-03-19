package dev.gridio.dnp3.codegen.model.groups

import dev.gridio.dnp3.codegen.model.FixedSizeField._
import dev.gridio.dnp3.codegen.model.VariationNames._
import dev.gridio.dnp3.codegen.model.{AnyVariation, FixedSize, GroupVariation, ObjectGroup}


//analog output events
object Group42 extends ObjectGroup {
  def variations: List[GroupVariation] = List(Group42Var0, Group42Var1, Group42Var2, Group42Var3, Group42Var4, Group42Var5, Group42Var6, Group42Var7, Group42Var8)

  def group: Byte = 42

  def desc: String = "Analog Output Event"

  def isEventGroup: Boolean = true
}

object Group42Var0 extends AnyVariation(Group42, 0)

object Group42Var1 extends FixedSize(Group42, 1, bit32WithFlag)(flags, value32)

object Group42Var2 extends FixedSize(Group42, 2, bit16WithFlag)(flags, value16)

object Group42Var3 extends FixedSize(Group42, 3, bit32WithFlagTime)(flags, value32, time48)

object Group42Var4 extends FixedSize(Group42, 4, bit16WithFlagTime)(flags, value16, time48)

object Group42Var5 extends FixedSize(Group42, 5, singlePrecisionWithFlag)(flags, float32)

object Group42Var6 extends FixedSize(Group42, 6, doublePrecisionWithFlag)(flags, float64)

object Group42Var7 extends FixedSize(Group42, 7, singlePrecisionWithFlagTime)(flags, float32, time48)

object Group42Var8 extends FixedSize(Group42, 8, doublePrecisionWithFlagTime)(flags, float64, time48)