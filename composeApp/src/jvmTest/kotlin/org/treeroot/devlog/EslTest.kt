package org.treeroot.devlog

import org.junit.Test

class EslTest {

    @Test
    fun doTest() {

        val service = org.treeroot.devlog.business.EsDslFormatterService()

        // 测试ES样本数据
        val sampleData = """
2026-01-22 16:48:08 DEBUG  org.elasticsearch.client.RestClient - request [POST http://139.196.125.242:9200/dev-xgy-searchdoc/_search?typed_keys=true] returned [HTTP/1.1 200 OK]
2026-01-22 16:48:08 WARN   org.elasticsearch.client.RestClient - request [POST http://139.196.125.242:9200/dev-xgy-searchdoc/_search?typed_keys=true] returned 1 warnings: [299 Elasticsearch-7.17.8-120eabe1c8a0cb2ae87cffc109a5b65d213e9df1 "Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.17/security-minimal-setup.html to enable security."]
2026-01-22 16:48:08 TRACE  tracer - curl -iX POST 'http://139.196.125.242:9200/dev-xgy-searchdoc/_search?typed_keys=true' -d '{"from":0,"query":{"function_score":{"boost_mode":"multiply","functions":[{"filter":{"term":{"status":{"value":1}}},"weight":5.0}],"query":{"bool":{"must":[{"match":{"name":{"boost":1.0,"query":"哈尔滨 "}}},{"term":{"docType.keyword":{"boost":1.0,"value":"GOODS"}}},{"range":{"startDate":{"boost":1.0,"gt":"2025-01-09"}}},{"range":{"endDate":{"boost":1.0,"lt":"2026-01-26"}}}]}},"score_mode":"multiply"}},"size":30,"sort":[{"_score":{"order":"desc"}},{"_geo_distance":{"location":[{"lat":39.904179,"lon":116.407387}],"order":"asc"}},{"status":{"order":"asc"}},{"startDate":{"order":"desc"}}]}'
# HTTP/1.1 200 OK
# X-elastic-product: Elasticsearch
# Warning: 299 Elasticsearch-7.17.8-120eabe1c8a0cb2ae87cffc109a5b65d213e9df1 "Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.17/security-minimal-setup.html to enable security."
# content-type: application/json; charset=UTF-8
# content-length: 12584
# X-Elastic-Product: Elasticsearch
#
# {"took":1,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":22,"relation":"eq"},"max_score":null,"hits":[{"_index":"dev-xgy-searchdoc","_type":"_doc","_id":"chh32psBTqiSE_5nK7tH","_score":38.96631,"_source":{"venueName":"哈尔滨工程大学综合体育馆","image":"https://oss.manxing.net/showImage/67e20e8be4b00e709374a6a6.jpg","endDate":"2025-05-02","mysqlId":6296660656570694707,"guestIds":[],"docType":"GOODS","name":"哈尔滨·ShiningHeart动漫展","location":"45.776936,126.677951","attendCount":0,"startDate":"2025-05-01","status":1,"openGroupBuy":false,"id":"chh32psBTqiSE_5nK7tH"},"sort":[38.96631,1060472.6135295979,1,1746057600000]},{"_index":"dev-xgy-searchdoc","_type":"_doc","_id":"tRh12psBTqiSE_5n7roZ","_score":37.482838,"_source":{"venueName":"玖禧花园宴会艺术中心·婚宴","image":"https://oss.manxing.net/showImage/67ce7410e4b02811165b8e55.jpg","endDate":"2025-04-06","mysqlId":6296652914941842662,"guestIds":[],"docType":"GOODS","name":"哈尔滨·DreamLand动漫游戏展","location":"45.687048,126.614471","attendCount":0,"startDate":"2025-04-05","status":1,"openGroupBuy":false,"id":"tRh12psBTqiSE_5n7roZ"},"sort":[37.482838,1050776.073323748,1,1743811200000]}]}}
    """.trimIndent()

        println("原始数据长度: ${sampleData.length}")
        println("\n--- 分离DSL和响应 ---")

        val result = service.separateDslAndResponse(sampleData)
        val dsl = result.formattedDsl
        val response = result.formattedResponse
        println("DSL部分长度: ${if (dsl.isEmpty()) 0 else dsl.length}")
        println("响应部分长度: ${if (response.isEmpty()) 0 else response.length}")

        println("\n--- DSL内容 ---")
        if (dsl.isNotEmpty()) {
            println(dsl.take(200) + if (dsl.length > 200) "..." else "")
        } else {
            println("未找到DSL")
        }

        println("\n--- 响应内容 ---")
        if (response.isNotEmpty()) {
            println(response.take(200) + if (response.length > 200) "..." else "")
        } else {
            println("未找到响应")
        }

        println("\n--- 完整的DSL ---")
        println(dsl)

        println("\n--- 完整的响应 ---")
        println(response)
    }
}