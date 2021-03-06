package hust.tools.ca.beamsearch;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.sv.ChunkAnalysisSequenceValidatorWithBIEO;
import opennlp.tools.util.SequenceValidator;

/**
 *<ul>
 *<li>Description: 测试验证序列合法性方法 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月6日
 *</ul>
 */
public class DefaultChunkAnalysisSequenceValidatorTest {
	
	private SequenceValidator<String> validator;
	private String[] chunkTags1 = new String[]{"O",  "O", "BNP_B", "BNP_E", "O",  "O",  "O", "BNP_B", "BNP_E", "BNP_B", "BNP_I", "BNP_E", "O"};
	private boolean[] actual1 = new boolean[]{ true, true, true,   true,    true, true, true, true,   true,    true,     true,    true,   true};
	
	private String[] chunkTags2 = new String[]{"BTP_B", "BTP_E", "O",   "BTP_E", "O",   "BNP_I", "BNP_I", "BNP_E", "O",  "O",  "BNP_B", "O",  "O",  "BNP_B", "O", "BDP_B", "BDP_I", "BVP_B", "BVP_I", "BDP_B"};
	private boolean[] actual2 = new boolean[]{ true,    true,    true,   false,  true,   false,   true,    true,  true, true,  true,   false, true, true,    false, true,    true,   false,    true,   false};

	private String[] chunkTags3 = new String[]{"BNP_I", "BNP_I", "BNP_I", "BNP_I", "O",  "BNP_I", "BNP_B"};
	private boolean[] actual3 = new boolean[]{ false,    true,    true,   true,   false,   false,  false};

	private String[] chuntag = new String[]{"O", "O", "BNP_E", "O"};
	private boolean[] actual = new boolean[]{true, true, false, true};
	
	@Before
	public void setUp() throws Exception {
		validator = new ChunkAnalysisSequenceValidatorWithBIEO();
	}

	@Test
	public void testValidSequence() {
		boolean expected = false;
		for(int i = 0; i < chunkTags1.length; i++) {
			expected = validator.validSequence(i, null, chunkTags1, chunkTags1[i]);
			assertEquals(expected, actual1[i]);
		}
		
		for(int i = 0; i < chunkTags2.length; i++) {
			expected = validator.validSequence(i, null, chunkTags2, chunkTags2[i]);
			assertEquals(expected, actual2[i]);
		}
		
		for(int i = 0; i < chunkTags3.length; i++) {
			expected = validator.validSequence(i, null, chunkTags3, chunkTags3[i]);
			assertEquals(expected, actual3[i]);
		}
		
		for(int i = 0; i < chuntag.length; i++) {
			expected = validator.validSequence(i, null, chuntag, chuntag[i]);
			assertEquals(expected, actual[i]);
		}
	}

}
