package com.evg.scheduler.utils;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.evg.scheduler.model.es.StationUpAndDownData;
import com.evg.scheduler.model.es.portstatusindex;
import com.evg.scheduler.model.es.stationActiveRecords;


@Service
public class EsLoggerUtil {

	@Value("${es.stationActivelogs}")
	private String SCHEDULERLOGS_INDEX;
	
	@Value("${es.portstatuslogs}")
	private String CREATEPORTERRORSTATUS_INDEX;
	
	@Value("${es.stationupanddowndata}")
	private String stationupanddowndata;
	
	@Autowired
	private ElasticsearchRestTemplate elasticsearchRestTemplate;
	
	@Autowired
	private utils utils;
	
	@Value("${elasticsearch.url}")
	String ELASTICSEARCH_URL;
	
	@Autowired
	private ElasticsearchOperations elasticsearchOperations;
	
	public void createOcppLogsIndex(stationActiveRecords logs) {
		try {
			Thread th = new Thread() {
				public void run() {
					IndexQuery indexQuery = new IndexQueryBuilder().withId(logs.getId().toString()).withObject(logs).build();
					elasticsearchOperations.index(indexQuery, IndexCoordinates.of(SCHEDULERLOGS_INDEX));
				}
			};
			th.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createOcppLogsIndexBulk(List<IndexQuery> in) {
		try {
			Thread th = new Thread() {
				public void run() {
					elasticsearchOperations.bulkIndex(in, IndexCoordinates.of(SCHEDULERLOGS_INDEX));
				}
			};
			th.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dummy(stationActiveRecords logs) {
		
	}
	
	public portstatusindex getEntityByPortIdWithTimestampSort(long portId) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                		.must(QueryBuilders.matchQuery("portId", portId)))
                .withSort(SortBuilders.fieldSort("ToTimeStamp").order(SortOrder.DESC))
                .build();

        return executeNativeSearchQuery(nativeSearchQuery);
    }
	
	private portstatusindex executeNativeSearchQuery(NativeSearchQuery nativeSearchQuery) {
		try {
			SearchHits<portstatusindex> search = elasticsearchRestTemplate.search(nativeSearchQuery, portstatusindex.class);
			if(search.getTotalHits() > 0) {
				SearchHit<portstatusindex> searchHit = search.getSearchHit(0);
				if(searchHit != null) {
					portstatusindex content = searchHit.getContent();
					return content;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

	public void updatePortStatusLogs(long portUniId,Date toTime) {
		try {
			portstatusindex findById = getEntityByPortIdWithTimestampSort(portUniId);
			if (findById != null) {
				findById.setCreateDate(utils.getUTCDate());
				findById.setToTimeStamp(toTime);
				createPortErrorStatusIndex(findById);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createPortErrorStatusIndex(portstatusindex pes) {
		try {
			IndexQuery indexQuery = new IndexQueryBuilder().withId(pes.getId().toString()).withObject(pes).build();
			String index = elasticsearchOperations.index(indexQuery, IndexCoordinates.of(CREATEPORTERRORSTATUS_INDEX));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createPortErrorStatusIndexBulk(List<IndexQuery> in) {
		try {
			Thread th = new Thread() {
				public void run() {
					elasticsearchOperations.bulkIndex(in, IndexCoordinates.of(CREATEPORTERRORSTATUS_INDEX));
				}
			};
			th.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createStationUpAndDownDataBulk(List<IndexQuery> in) {
		try {
			Thread th = new Thread() {
				public void run() {
					elasticsearchOperations.bulkIndex(in, IndexCoordinates.of(stationupanddowndata));
				}
			};
			th.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public StationUpAndDownData getStationUpAndDownData(long stationId) {
        try {
        	NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.boolQuery()
                    		.must(QueryBuilders.matchQuery("stationId", stationId)))
                    .withSort(SortBuilders.fieldSort("startTimeStamp").order(SortOrder.DESC))
                    .build();

            return executeStationUpAndDownData(nativeSearchQuery);
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
	private StationUpAndDownData executeStationUpAndDownData(NativeSearchQuery nativeSearchQuery) {
		try {
			SearchHits<StationUpAndDownData> search = elasticsearchRestTemplate.search(nativeSearchQuery, StationUpAndDownData.class);
			if(search.getTotalHits() > 0) {
				SearchHit<StationUpAndDownData> searchHit = search.getSearchHit(0);
				if(searchHit != null) {
					StationUpAndDownData content = searchHit.getContent();
					return content;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
}