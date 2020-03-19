package dev.gridio.dnp3.codegen.model.groups

import dev.gridio.dnp3.codegen.model._
import dev.gridio.dnp3.codegen.model.FixedSizeField._
import dev.gridio.dnp3.codegen.model.VariationNames._

// counter event
object Group22 extends ObjectGroup {
  def variations: List[Variation] = List(Group22Var0, Group22Var1, Group22Var2, Group22Var5, Group22Var6)

  def group: Byte = 22

  def desc: String = "Counter Event"

  override def groupType: GroupType = EventGroupType
}

object Group22Var0 extends AnyVariation(Group22, 0)

object Group22Var1 extends FixedSize(Group22, 1, bit32WithFlag)(flags, count32)

object Group22Var2 extends FixedSize(Group22, 2, bit16WithFlag)(flags, count16)

object Group22Var5 extends FixedSize(Group22, 5, bit32WithFlagTime)(flags, count32, time48)

object Group22Var6 extends FixedSize(Group22, 6, bit16WithFlagTime)(flags, count16, time48)
