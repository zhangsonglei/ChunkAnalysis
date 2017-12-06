package hust.tools.ca.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import hust.tools.ca.beamsearch.ChunkAnalysisBeamSearch;
import hust.tools.ca.beamsearch.ChunkAnalysisSequenceClassificationModel;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.model.BaseModel;

/**
 *<ul>
 *<li>Description: 组块分析模型 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisModel extends BaseModel{
	
	/**
	 * 训练模型的类
	 */
	private static final String COMPONENT_NAME = "ChunkAnalysisME";
	
	/**
	 * 
	 */
	private static final String CHUNK_MODEL_ENTRY_NAME = "ChunkAnalysis.model";
	
	/**
	 * 构造方法
	 * @param componentName		训练模型的类
	 * @param modelFile 		模型文件
	 * @throws IOException 		IO异常
	 */
	protected ChunkAnalysisModel(String componentName, File modelFile) throws IOException {
		super(componentName, modelFile);
	}

	/**
	 * 构造方法
	 * @param encoding				编码
	 * @param maxentModel 			最大熵模型
	 * @param beamSize 				大小
	 * @param manifestInfoEntries	配置的信息
	 */
	public ChunkAnalysisModel(String encoding, MaxentModel maxentModel, int beamSize,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, encoding, manifestInfoEntries, null);
		
		if (maxentModel == null)
            throw new IllegalArgumentException("最大熵模型不能为空!");

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);
        manifest.setProperty(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER, Integer.toString(beamSize));

        //放入新训练出来的模型
        artifactMap.put(CHUNK_MODEL_ENTRY_NAME, maxentModel);
        checkArtifactMap();
	}
	

	public ChunkAnalysisModel(String languageCode, ChunkAnalysisSequenceClassificationModel<String> seqPosModel,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (seqPosModel == null) {
            throw new IllegalArgumentException("The maxent wordsegModel param must not be null!");
        }

        artifactMap.put(CHUNK_MODEL_ENTRY_NAME, seqPosModel);		
	}

	/**
	 * 获取模型
	 * @return 最大熵模型
	 */
	public MaxentModel getChunkAnalysisModel() {
		if (artifactMap.get(CHUNK_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            return (MaxentModel) artifactMap.get(CHUNK_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
	}
	
	@SuppressWarnings("unchecked")
	public ChunkAnalysisSequenceClassificationModel<String> getChunkAnalysisSequenceModel() {

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);

        if (artifactMap.get(CHUNK_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            String beamSizeString = manifest.getProperty(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

            int beamSize = ChunkAnalysisME.DEFAULT_BEAM_SIZE;
            if (beamSizeString != null) {
                beamSize = Integer.parseInt(beamSizeString);
            }

            return new ChunkAnalysisBeamSearch<String>(beamSize, (MaxentModel) artifactMap.get(CHUNK_MODEL_ENTRY_NAME));
        } else if (artifactMap.get(CHUNK_MODEL_ENTRY_NAME) instanceof ChunkAnalysisSequenceClassificationModel) {
            return (ChunkAnalysisSequenceClassificationModel<String>) artifactMap.get(CHUNK_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
    }
}


