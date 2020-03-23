package dev.gridio.dnp3.codegen.render.modules

import dev.gridio.dnp3.codegen.model._
import dev.gridio.dnp3.codegen.model.groups.{Group1, Group1Var1}
import dev.gridio.dnp3.codegen.render._

object RangedVariationModule extends Module {

  override def lines(implicit indent: Indentation) : Iterator[String] = {
    "use crate::app::range::{RangedSequence, Range};".eol ++
    "use crate::app::gen::variations::fixed::*;".eol ++
      "use crate::app::gen::variations::gv::Variation;".eol ++
      "use crate::util::cursor::ReadCursor;".eol ++
      "use crate::app::parser::ParseError;".eol ++
      "use crate::app::bytes::RangedBytesSequence;".eol ++
      "use crate::app::bit::IndexedBitSequence;".eol ++
      space ++
      rangedVariationEnumDefinition ++
      space ++
      rangedVariationEnumImpl
  }

  private def rangedVariationEnumDefinition(implicit indent: Indentation) : Iterator[String] = {

    def getVarDefinition(v: Variation) : Iterator[String] = v match {
      case v : SingleBitField if v.parent == Group1 => s"${v.name}(IndexedBitSequence<'a>),".eol
      case v : AnyVariation => s"${v.name},".eol
      case v : FixedSize => s"${v.name}(RangedSequence<'a, ${v.name}>),".eol
      case v : SizedByVariation if v.parent.isStaticGroup =>  {
        s"${v.parent.name}Var0,".eol ++
        s"${v.parent.name}VarX(u8, RangedBytesSequence<'a>),".eol
      }
    }

    "#[derive(Debug, PartialEq)]".eol ++
      bracket("pub enum RangedVariation<'a>") {
        variations.iterator.flatMap(getVarDefinition)
      }

  }

  private def rangedVariationEnumImpl(implicit indent: Indentation) : Iterator[String] = {

    def getNonReadVarDefinition(v: Variation) : String = v match {
      case _ : AnyVariation => ""
      case _ : FixedSize => "(RangedSequence::parse(range, cursor)?)"
    }

    def getReadVarDefinition(v: Variation) : String = v match {
      case _ : AnyVariation => ""
      case _ : FixedSize => "(RangedSequence::empty())"
    }

    def getNonReadMatcher(v: Variation): Iterator[String] = v match {
      case Group1Var1 =>  {
        s"Variation::Group1Var1 => Ok(RangedVariation::Group1Var1(IndexedBitSequence::parse(cursor, range)?)),".eol
      }

      case v : SizedByVariation => {
        s"Variation::${v.parent.name}(0) => Err(ParseError::ZeroLengthOctetData),".eol ++
        bracketComma(s"Variation::${v.parent.name}(x) =>") {
          s"Ok(RangedVariation::${v.parent.name}VarX(x, RangedBytesSequence::parse(x, range.get_start(), range.get_count(), cursor)?))".eol
        }
      }
      case _ => s"Variation::${v.name} => Ok(RangedVariation::${v.name}${getNonReadVarDefinition(v)}),".eol
    }

    def getReadMatcher(v: Variation): Iterator[String] = v match {
      case Group1Var1 =>  {
        s"Variation::Group1Var1 => Ok(RangedVariation::Group1Var1(IndexedBitSequence::empty())),".eol
      }
      case v : SizedByVariation => {
        s"Variation::${v.parent.name}(0) => Ok(RangedVariation::${v.parent.name}Var0),".eol
      }
      case _ => s"Variation::${v.name} => Ok(RangedVariation::${v.name}${getReadVarDefinition(v)}),".eol
    }

    bracket("impl<'a> RangedVariation<'a>") {
      "#[rustfmt::skip]".eol ++
      bracket("pub fn parse_non_read(v: Variation, range: Range, cursor: &mut ReadCursor<'a>) -> Result<RangedVariation<'a>, ParseError>") {
        bracket("match v") {
          variations.flatMap(getNonReadMatcher).iterator ++ "_ => Err(ParseError::InvalidQualifierForVariation(v)),".eol
        }
      } ++ space ++
        bracket("pub fn parse_read(v: Variation) -> Result<RangedVariation<'a>, ParseError>") {
          bracket("match v") {
            variations.flatMap(getReadMatcher).iterator ++ "_ => Err(ParseError::InvalidQualifierForVariation(v)),".eol
          }
        }
    }

  }

  def variations : List[Variation] = {
    ObjectGroup.allVariations.flatMap { v =>
      v match {
        case Group1Var1 => Some(v)
        case v : AnyVariation if v.parent.isStaticGroup => Some(v)
        case v : FixedSize if v.parent.isStaticGroup => Some(v)
        case v : SizedByVariation if v.parent.isStaticGroup => Some(v)
        case _ => None
      }
    }
  }

}
