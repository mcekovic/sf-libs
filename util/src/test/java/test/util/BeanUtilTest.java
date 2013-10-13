package test.util;

import org.junit.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;

public class BeanUtilTest {

	@Test
	public void test() {
		TestBean bean = new TestBean();
		String zika = "Zikica";
		BeanUtil.setProperty(bean, "zika", zika);
		String mika = "Mikica";
		BeanUtil.setProperty(bean, "mika", mika);
		String laza = "Lazica";
		BeanUtil.setProperty(bean, "LAza", laza);

		assertEquals(zika, BeanUtil.getProperty(bean, "zika"));
		assertEquals(mika, BeanUtil.getProperty(bean, "mika"));
		assertEquals(laza, BeanUtil.getProperty(bean, "LAza"));
	}

	@Test
	public void copyPropertiesTest() {
		TestBean source = new TestBean();
		source.setZika("Zika");
		source.setMika("Mika");
		source.setLAza("LAza");
		TestBean target = new TestBean();
		BeanUtil.copyProperties(source, target);
		assertEquals(target.getZika(), "Zika");
		assertEquals(target.getMika(), "Mika");
		assertEquals(target.getLAza(), "LAza");
	}

	public static class TestBean {

		private String zika;
		private String mika;
		private String LAza;

		public String getLAza() {
			return LAza;
		}

		public void setZika(String zika) {
			this.zika = zika;
		}

		public void setLAza(String LAza) {
			this.LAza = LAza;
		}

		public String getZika() {
			return zika;
		}

		public void setMika(String mika) {
			this.mika = mika;
		}

		public String getMika() {
			return mika;
		}

		public String toString() {
			return "Zika: " + zika + "\nMika: " + mika + "\nLaza: " + LAza;
		}
	}
}
