package com.getjenny.analyzer.expressions

case class AnalyzersData(
                          private_data: Map[String, String] = Map.empty[String, String],
                          item_list: List[String] = List.empty[String],
                          extracted_variables: Map[String, String] = Map.empty[String, String],
                          data: Map[String, Any] = Map.empty[String, Any]
                        )

case class Data(
                 item_list: List[String] = List.empty[String],
                 extracted_variables: Map[String, String] = Map.empty[String, String]
               )

