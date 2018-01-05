package hust.tools.ca.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.model.BaseModel;

/**
 *<ul>
 *<li>Description: 基于词的组块分析模型 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordModel extends BaseModel {
	
	/**
	 * 训练模型的类
	 */
	private static final String COMPONENT_NAME = "ChunkAnalysisBasedWordME";
	
	/**
	 * 
	 */
	private static final String CHUNK_MODEL_ENTRY_NAME = "ChunkAnalysisBasedWord.model";
	
	/**
	 * 构造方法
	 * @param componentName		训练模型的类
	 * @param modelFile 		模型文件
	 * @throws IOException 		IO异常
	 */
	protected ChunkAnalysisBasedWordModel(String componentName, File modelFile) throws IOException {
		super(COMPONENT_NAME, modelFile);
	}
	
	public ChunkAnalysisBasedWordModel(File modelFile) throws IOException {
		super(COMPONENT_NAME, modelFile);
	}

	/**
	 * 构造方法
	 * @param encoding				编码
	 * @param maxentModel 			最大熵模型
	 * @param beamSize 				大小
	 * @param manifestInfoEntries	清单中的其他信息
	 */
	public ChunkAnalysisBasedWordModel(String encoding, MaxentModel maxentModel, int beamSize,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, encoding, manifestInfoEntries, null);
		
		if (maxentModel == null)
            throw new IllegalArgumentException("最大熵模型不能为空!");

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);
        manifest.setProperty(BeamSearch.BEAM_SIZE_PARAMETER, Integer.toString(beamSize));

        //放入新训练出来的模型
        artifactMap.put(CHUNK_MODEL_ENTRY_NAME, maxentModel);
        checkArtifactMap();
	}
	
	public ChunkAnalysisBasedWordModel(String encoding, MaxentModel maxentModel, Map<String, String> manifestInfoEntries) {
		this(encoding, maxentModel, ChunkAnalysisBasedWordME.DEFAULT_BEAM_SIZE, manifestInfoEntries);
	}
	
	/**
	 * 构造方法
	 * @param encoding					编码
	 * @param chunkClasssificationModel	组块分析分类模型
	 * @param manifestInfoEntries		配置信息
	 */
	public ChunkAnalysisBasedWordModel(String encoding, SequenceClassificationModel<String> chunkClasssificationModel,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, encoding, manifestInfoEntries, null);
		if (chunkClasssificationModel == null) {
            throw new IllegalArgumentException("组块分析模型不能为空!");
        }

        artifactMap.put(CHUNK_MODEL_ENTRY_NAME, chunkClasssificationModel);		
	}

	/**
	 * 返回组块分析模型
	 * @return 最大熵模型
	 */
	public MaxentModel getChunkAnalysisModel() {
		if (artifactMap.get(CHUNK_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            return (MaxentModel) artifactMap.get(CHUNK_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
	}
	
	/**
	 * 返回组块分析分类模型
	 * @return	组块分析分类模型
	 */
	@SuppressWarnings("unchecked")
	public SequenceClassificationModel<String> getChunkAnalysisSequenceModel() {

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);

        if (artifactMap.get(CHUNK_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            String beamSizeString = manifest.getProperty(BeamSearch.BEAM_SIZE_PARAMETER);

            int beamSize = ChunkAnalysisBasedWordME.DEFAULT_BEAM_SIZE;
            if (beamSizeString != null) {
                beamSize = Integer.parseInt(beamSizeString);
            }

            return new BeamSearch<String>(beamSize, (MaxentModel) artifactMap.get(CHUNK_MODEL_ENTRY_NAME));
        } else if (artifactMap.get(CHUNK_MODEL_ENTRY_NAME) instanceof SequenceClassificationModel) {
            return (SequenceClassificationModel<String>) artifactMap.get(CHUNK_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
    }
}


