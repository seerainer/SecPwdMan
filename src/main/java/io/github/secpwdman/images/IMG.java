/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
 * philipp@seerainer.com
 * http://www.seerainer.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
package io.github.secpwdman.images;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * The Class IMG.
 */
public class IMG {
	public static final String APP_ICON = """
			iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAwElEQVR42u3WQQqDMBAF0Eyhm9JV
			L9kzeci6Kt0ITlMqqCRx/jdVKkwgCDr+eQaVSFgzGtXs+bsIG8XdUGpcAcEBaHMSgQHY5iE847wi
			CBuQNn/FeYnzFBvoUPPJ6SfX4JVYAyiHfmt1lvtzgLWsZL0DHOAAgULS0Q7Hm/kABiYFNPqggysQ
			cwD/z69GOOAYAD2PZdItG6FaBjANtILhWhSQCywFM7UOOM47kAve/SvYZDjgfwF7IBb3A1sjMjui
			N+62qyFnP/JjAAAAAElFTkSuQmCC
			"""; // $NON-NLS-1$
	public static final String DEL = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAALsSURBVDhPfZLfaxRXFMfPvfN7du7dnTHiW1tU
			RGoWTEqxlUCUPjfNEh/8ga8iJQ+CCBpBpD70wT+gRXyRNa34skHfFaENTQop1kKhD6HVINlkd2Z2
			fuz8vtOZaVwNQj8wMy/n+znn3Llo45NLTbmh3pIU1Ut9tqA/vfES/odX4/N1eUz9pravfljSpNto
			e/rqI5FIX8pUhcTLfksGcUt/dvOfnfpdbIx/TUVDbct6bUauq5Dn+Z9YVNRAkGTAPAaR8hMC5Tr9
			qesf7mRGvG5epLKhtBVdnZGpAlkYw7BrBziP4FoWsD/SIAOEEYh1cUKsC53e51dGks3mBSrpStVZ
			KibNogRC09mMLXcelQXW9M0DnIKXBI0b51UBWMYgMr212HRbme+ZgkEWJZ3MCLQGLI4h7DubycCf
			2/vrd8uVoMScWjjA17hCIhYSERjLIOoPnmfD0BQNclIkGmRFOO4PNmPHnxtb/X65zI0EJebxy4VE
			6AhEavI1uTykomMCnFQIy7BpV52N1TtVuGSXoMT6bH4/XxMfCzr5mCe14lwwsDCCaKu3lTpuq7Fy
			dxQuwTvfEWhobUEabaFSXb6KKYDDUHgcnMUb/1W9ZZfAOXqOCAa9L+j0BCdLkEcRZEMfUCER9hgH
			hTGj4316/oOd8orRCt7RswQ3aJtrNL7iKIW82DmzzO08TX2O1j/CmgZ58Xcys7fGzH5LXb1f3dhq
			An/iNOEapM2/E2ZWr5s7dqt4vmCO/YJ5LqBiFc7YM8kZRic4draaBEWTZyjUyT3c0GcRqQPEURVm
			rnNKXm7/VBaFx87sx4QuIdJoIo1CcVGAmdtrudVr4Zjyt5imzCKNVOHc7nXBtUfhEnnlx3VwB7Pg
			Wi/Ad6rDZUSbTKhyBwdCemSIQ0iCAYC93UWudUpcXhyF3yCuPFhHnj0Ljvl76vYgTBwIhPwQDllw
			Owmdv7JB9znyrDn+5x/eC7+B/+XhOvLtFvP6T9Kh/XecDr/9F1llXKAeIDOEAAAAAElFTkSuQmCC
			"""; // $NON-NLS-1$
	public static final String EDIT = """
			iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAYAAAAmlE46AAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAJLSURBVDhPfVFNTxNRFD1vZjozHSgl1KKTGBUS
			IwhWVqQxMYatMcrWnbo0MSbu+BNuXLgxYeXChSGuXbgiiBpj2mBNUSsYFGmZUjof7WPmjXemI0gk
			3pczN+/eOffjHfb148acpqnXAAjCP9bpepbDdx8Upia/JKHYWLW0vqzr6nRyP2SMTqvVgu21Stmh
			zOz45FgtSUEKw1DQwVEQIfkQ6Lq8sNu0Fyorn84kPEhRhshHQwhIkgRFSUXki23LWaiUq6djYlz5
			P9B1HcdywxjOm8j2D01psnGnR0yqc96FbbcJ9iE4jgO+14UsU2dVBZMV5YBII/Eu7/3sRISowAHa
			FPd9H9q3VQwsvsxGRLby9vOSmkoVHc+l6jZFGGiEKJcY3SUGc2cL5vNHULV0WxrMzVJHEpA+iqzA
			SPfB0I2ej2D0oT8lIxVyuCdOAmOXohEzcJ35ZNQQLnVsbNfRsBqxr5Nvb9RgvnmGkdIr9A0cx+7V
			2/Bzo1103Ces9Lq6pMhyMQh87NEevekYMu428u+fQv35AWEnQOfyHKzxK5Bbm49HZybuxnJEXT3P
			Q3PHIjTBVhdhvnsIwy1DNlTQHgiqy9izN8EHjWZUe/9Vo51M0us8X8eE9QJphQpoCkKfXnvkOhoz
			NxGQHFGjfeIfsQ2rgvz3eSipDiAzCC5gnb2Frekb8NV03OBvIovHFbQH30E7UwALfQRcw6+xe6jT
			XkKSY0JvLUH6EBEhakEgSEMX1fIGfjRPYTsoYu3cfdTNCwiCEIEvyCfwxRoA/AZe65Yyx/C+rQAA
			AABJRU5ErkJggg==
			"""; // $NON-NLS-1$
	public static final String GEAR = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAMOSURBVDhPdVNLbFNHFL1vfs92sJ0ozgsNlI8E
			Ef+f6IJKVRoKYkEDLApiAwg2bEACBBKfBTs2LNmyAoRgA6UNCAQoUAHFpCKYT1KgBQQJsWQS2yH2
			+8yPGStYyoIjjd6deXPuzDn3jgPfQPbuwOx4LHGKUpagLj48Z6H3aOLXJKCJbw0DfUPfjXwaJzYm
			iO7EiK43YafjOPvtmsXL3LA3+LZY59WDnu6nR4MK7x8dHv+zv29wnlRqeRj5wHkEIhLznva+m5HL
			vjmtlHohInmqkP9MLa8mofvi3y3pVLqfMZahjABCTlFpEXcwxDBBds7N1gJBrA0hM8dIUoZ+mNne
			3Fe7Qe8/D0e01n9xzkFwCcXiaNPYWDkWBiGA1uC6Lo3FWZsCAUppUFI956H833LrJt683DfLZSz7
			uVL2grACUsoKIfR2c3NTkPEyq5OphoyQAkJfcLO+bu5ir8fynFuXnsw2Tu/CBC8Ow2BtvvA+IQSv
			gsRbduzZeNVuenTn32WppmQ3pWRaFHLpAO4hFD+jLjprEuT+oIx1YaOtVBqFQmkICCZXt+3e9Ksl
			f8Xj+/+ddGPsoJEKoAhYbwhF/ci4ndBWlxlCSKPZgXjCNeInI7DwA1MVCeYW1geIAhlDURQeMeOC
			uXaOEsZBItAKfr517f7KCS7cuPxgOo/4liCIQAlTSKkHolBcCarR3rqJ1y72zmTUzeY/DrVK4JBM
			J/JunJ7X2vGxQzYTQtopo5BKNvom7lyy6vus5dUbCSNyHCHc2uK1gmM0jo1Wp5ZH/AP+eHQs8KN2
			W16XJUxVnTgizomP74uu5dUS/H7mXosDqEsrBZS50DZtetCYbhYUuYAdCol4g04lmwJziPHKahcd
			pUJ1QT2BgMA0Epwz2rg56bHk+pdUqvF6xvOgxZsKU6akX4tIdxriHRFKHfr8elDlr+sJftu+RpWG
			K4fM4opqOexYt3XpA3PSMylUzW0zXnVsmP/ww8vC5pH82I+FwfLWFT/NGrfcbz7nGxdyi8wbOGfq
			3YAp3tfRNb/WVJMB8AVxHYQpREEr2gAAAABJRU5ErkJggg==
			"""; // $NON-NLS-1$
	public static final String KEY = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAInSURBVDhPjVJNaBNREJ55b9PsJpWmUalWrVSD
			IIqIN8WDB6GJ4F0UivZkT0V68VAE70XqJRe9FDFiUU9eFIs/eBHJTZH4Ew9CiYKmNNnft/vG2U0u
			xa7kY4edx8x88828hzAA1JupM5iFS5iBElqyjSP5J7B7fAXxdvBfgvqN0+LIqfxNmYPraILBJAAZ
			Ai0wlMXCI9w5NiN6qVtj8qh1gTsssGsgZ6IBEGVkde37xgnn5/ox6K5fSyW4CvukEDCLwBTEHxsg
			glG0mvLweAMj1QLHmUkd4f3iycKhg8MfTFPuwSyBsLjeIsBt0iGENQzDA2gNc48UdLvKI6B27FPE
			FrD5CNSJcuhGJVAoKNAbqQQTx3dYMm8oMGLtADpk85jERdAOE/GfPFrdcoTm43Jhe17UrIyuoOQi
			zVqYIF5HfI5NA/yiXGaK3c1oPqwUilnxwAKqYK85RBpUqLTHowyxEt9z6Z1rR1dGzj6rb1Lw7X5l
			tDiENVNQWcTU8bWR9jqOmnX88C0S7g0C+t34aDfOL9V5K4moHr4unxstGlAzgcqJROQVIXVsL5zf
			Nb263E/7BwnBl7tcLLkzcbHgl8baO2540fbVy9Lc61aSmQL5qcoLA1EzA56ZNxOF0PaVrgZBdGdy
			7tWffl4qsLVUWcwDzicHntn2w3tj88+nk+gAEGRH++M7iU355PuuftoLDQbheuEt148+s+wfnY66
			PLHwYqUfGwAAfwFmeeyX/q34wgAAAABJRU5ErkJggg==
			"""; // $NON-NLS-1$
	public static final String LINK = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAMLSURBVDhPhVJrSBRRFL4zd2afs2turrVZSAUV
			BD2gP1n2XCvfbqVoavRA+mGE9Phn/ehHRAhRERtEJPSiIFHUIoyegpWvdW0zksRan7u6r5ndnbkz
			s9Nd2SJB64PDuXz3nMN37v0I8B84P7hoAIgyHIdIkrRACCdkWXrsnZy+vzN/C/rngJ52p44kyFv4
			WImQBCRR9qtUqgVJyQZCluWHk2Oe4/MO6H7Xp1UUcEeS5DIuFHGFWb4G58+QgqtNKcZr6SvT1iNB
			rJlzQF+HSytL8m1BEMv90yFnKBCxlZ7IHUpcg0f2lk0rVi1tT0o29pMJbhZisdh5BYByUZR8eO/q
			v5vjiIaFAVESRxVFscxS4Pw4oCYAUUsQxFkcGoqCgKKoSYTEm16P9/K27M1ivK7l0asVKeYFvYyB
			cf9R0PnWoZaQZEcCqvVPBzTu4fGBocGR+2MjHkjR1EVzqvlivO7J7Wd6rU5dhzkjHxVaZhR8et2r
			xnLsOI5yoagPZxOkYeOu/AzbveuNG5YtX9yaalloHHWP747J4Axj1JYQAHaG2Wgu2fXeAUlI3sBz
			jvJR9IUNRvLUavWwyZRk7Wjr2ll5qsjhmfDVj7k9DPbCUz2jKZHFWA8bCB/MOrDFSyoxUITNUSXL
			sW/4n222I3s6wqxQhwSZoWlVQ/vzzlvJCw0bISSAVqdZKiK5h2Ojtr0lmT/j6knES8X4wQCWfc5q
			2/otTr5+4LR7xvynuRCPCAKe0Ol02RRFAxSVmvGKhTmlO2aa48ADxCV4Bx4owJXgwJXm6lhexfar
			oz+8mb6pwNcwFwGhIPdyeHD8YH7FrpFE2QxIjuVHCJLUMEb9ugQ3g4a7L7SpacYLJjOzRqWh+rF1
			q46d248S139APLzRWpC+ytK0yGIaYkNc1ZTH302rKDOE5CWciwVe7A/6uaKCCussM/0GXLfYOqgx
			kMkUDffpGV2l3qCr1GhVNRRNb8S/4gj42APzNccB3zialIy1OW2iKH7n2IgJm8MgRNFEJMzXB33s
			ycLDWe5E7RwA4Be1l3BVNBHJmQAAAABJRU5ErkJggg==
			"""; // $NON-NLS-1$
	public static final String LOCK = """
			iVBORw0KGgoAAAANSUhEUgAAAA0AAAAQCAYAAADNo/U5AAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAIlSURBVDhPXVHPaxNBFH4zszPZ3XRtBSu2Gnpo
			Szz0oN5sMAeR4sFCLx78BwTxUBC8eVPw5kUoithTUap4UxDxVAmCP/AgHlQaL73YEm2yye5mJzvj
			m80kDT748jbf97339r0l8F9sbr44O+YH17kj5oXLdygjj6rVxddWzoPanMfTJ8+XD3kTbwtpsAz7
			bqZb4hwH91Wt9uGqteQxLFpbe1j0XP+uyIq/k121uHTlXaW5HZ9p73Y/EU1ub23Vpqz1oMj3/BMF
			USg73Nm4dK3yDeCmXrlR3Wk2mw/iTnKUUFq21oMiRagDDH8C0rJUHvvpnzCMWqCBCEsB+blRnS3N
			ebeIE8yHcq7i8/C7S7frWjMCWus4HZ+KstKpgO99Fk6jTmTxHvm1fuFZ6aR7GYdgDwWg8aDKAB+z
			PgWZwmfkUCPC+0KZYseoMWoUexS0JJBJBxoNAUmIdBeRIp/i4MQ00pNUo1n3jNAHKA5vvs7A6uMZ
			WH95GOJQ9/mhDpoCvsKgwEB2GXz84UMkKbyvc2ibs4zoxp8XGXIgOEkCS7N7ECUpXCy3YUJoUIMC
			k3FHapYddjEZd5gO8MSyA8fHY2DmKPle1pNPGtlnAJZKEKoNHsW25gDGbDVzzf5OcqQIux5hEu6v
			xLAwSUCNTMlhJpHE6RrjoJNpQBDTvgZuvpXlch7fiiiQbPX0wl8qxXmICmNZh0MPYXKOCP/HmBOO
			EzlWOiEL6J1/BaQmzWCLMzoAAAAASUVORK5CYII=
			"""; // $NON-NLS-1$
	public static final String NEW = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAI1SURBVDhPlZJNaBNBFMffzOxsNsk2UGviNqjr
			V0AtOaQWPPfgzZsHD+qlXkykevNSFDwoBSlYLCJCLULBS+N6EEQPehC9aC8KglqIiEir2GTrRnd2
			dnacjSEgTT/8saf33u//hplF8B/8PD58CCNyi2i0asw+Go9ruNXZBN7R/YMI4fuEaENasueqPD8y
			Ftc3dYIXL+cHrdpHJ//k3k4qJRDdADAzEnhwkbRn1uTVs+clRJOO2GbbtL8AZu0doJADcI5AhIfX
			DXCvHCuZS0uOsA7Y1OwDsHYBtvZB4sM8gOCBlHJ0zYDGtRMl3PQd/ct7O/V9GWDvEND0FvAzWfDT
			VpBaeF3BM3PTXQMaE7HsOcSt2/ElkZVPoH+rA9teBI/JwE31VvrPjUzHs6sC3OsnS+S359Bm3cY4
			AkQQqGsD+FEDseiyZq5wtjhst+SYf57RnVQyU3LQsElCPZGBVYBs9bjWx9DXt5WBI4WOHNMJcG+c
			Kmncc3TRsDX1SiihZNWVkYQQTCaQUcndrt5pj3doBbhTSg6VLF21GQHSCSAkQQoBYZhkoTQq2Ztz
			q+QYvHz3QpGKppOIZV1tpZoqt+WAsjAyyn1TD7rKMdg186dFT17JajOlqhSBVD+KYIiJMFHunXw4
			83e0O5hLsBaNHfArPaAWc/UFEPkRCzktZyYeryvH4JWGn+Megc/N3eDJg0oOmeDaGXP86YZyDMH1
			7OieXDagYXLB87e+SWnksnmpOtvubwDAH8V14XLEwbzCAAAAAElFTkSuQmCC
			"""; // $NON-NLS-1$
	public static final String NOTE = """
			iVBORw0KGgoAAAANSUhEUgAAAA0AAAAQCAYAAADNo/U5AAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAGPSURBVDhPjVLLTsJAFL3zaA1JSzTwFyw0xp0J
			v+DKjRu/wI/xD0z8AXeuTdxqogkLMYQENEgBpQUU2umM905bAmoMZ3LamTNz5j5a1u+Mz4SURwCg
			kWtgyMVioYPR2/neQe06U1Hvtce3UopDkwuryEwxBKNeJBx2urtfuyKd4+H0LwPB6ugUXJRTpS8f
			7xvHJHEwuPUv0SQcEEx6KjEXD3eNE/ba+rjB9Op0w18gXxzHYLRBvwHGoM3zPVAqgels+ouzzykk
			KgYuGJRKJaLE9MjC8LYEwjCEKIogojeS1gW/5nOMirGQ1AibN5HZfiEwh2yVDSvZZwbbCHJLxwHP
			98H3fPCIOC/oe2Vw3a1lJPby/I6NkPVoEkIQ9PMoNn4GnFIDdrYrUK1USeiybpNMoq6Usl0qQDYh
			OLiOa9esSJlDN+8egzkWOhgOYDjKOQxgMpngYW65rIpq7zxhJIHfCW9aLZZgk8QaVpFFouJwYYwG
			/YOk0e7awMNZXJpZ5wYEw7jWpqlUin/EZkyVbn0DORoaRJDkYCgAAAAASUVORK5CYII=
			"""; // $NON-NLS-1$
	public static final String OPEN = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAOCAYAAAAmL5yKAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAFmSURBVDhPlVI9SwNBEH0zd2cSBUtbf0bAzq9K
			RElhIZb+BFv/g2AtoohlhGgjGkQRUVJqZSFobSGCQrz98G0u0UDuMA73bu5m37ydmV1Jb2trmmBd
			0DXvs8fJQVyt73SjhSa2VWtq7GdC1g9AOVHrDDai6tFWRs03sXfLTY1cn0BvhSIehqFD+idYJ3AO
			4i28c6/WRLvJ/OmH2JvFpmpXIJjrKQQLlQQhxpjMTHrb4XiJlnT6rCH2eqGpYilAvuVrLAHKcZY/
			YH3ikT6T31CWxkQiNcAIE8dH6UcKUPpFlEzCyWomYNhX8BUuGJY8FFiFxYWibeDb7Eu5O88zn1wA
			J8eKlH0RkpSpqAQXhoHBGxwu1aeOw2UgCOTtUowW3usvKhSQmL37f5Zv5URm4ZVXBShx8qH8PGI+
			Psk/D6epnPy9RBXOgX/55EFYeSAeg4D4q5UJfJW3+T3VEfzbeGTYlLn9PQD4BpkN5Z4Cqpj8AAAA
			AElFTkSuQmCC
			"""; // $NON-NLS-1$
	public static final String SELA = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAEeSURBVDhPldNNK4RRFMDx4y0kymJS7NTslC9A
			PgOJUuZ7+ATWFpSdlYVsZKXEwsbCQpqGmMbKwmsYMwuT+P818wHOqd/T6em597nn3nN7ImIbUzjH
			HgZRwyGe8I0DXGICuzjGLHa6efjxA35xi2f84BofcIIKmmi0c9+9o9zFoxcOdlAm/LkVxD7WTZKx
			iJozDOAGd8iEK69bwghcvvVloh9DJkfYMEnGCt4swaO6gCeRCVd85QQtvLZfZMKyP01OsWWSjFU0
			3cRp+PcqMjGGYqcZ/hsiGZ0mjBNsmiTDEhrOMg430juQiWEUXLq3yobwGBfQBy/KMtwbd3sJj3DQ
			PO7hHswgzuCVdqDtvIZRvMBmKeILfjyHOiZRiojWH/+zRoH14RpQAAAAAElFTkSuQmCC
			"""; // $NON-NLS-1$
	public static final String SAVE = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAJZSURBVDhPjVM7aBRBGP5m9nF7dyEiEmIgnU8Q
			vUYLC0URg0FFDwsLFVIELC0stLAQlES5RuzF3srSwkqwiNVFC/FR3CXhjEgg98zd7jz8ZvdO0qj5
			4dvZnfnm+58rrl56dnz35FSl292YrC29hWlb3H5wHVfKF2GthbM4jvHty1fkC3loY0Tl+ZtPR44e
			u7OwcPmHDykXE+Wd6Q8AQ7LmHSl9xIMEhgJCAOvrP/Fo/gkOndqP8s0ylIkObzYH0/fvvbompZAT
			MJq3NSzZ1hPQWiNRCsoh4TuhdkVQQvLMkGrQaeuT7bZ9KWEMrFaEhqE3QxGGObw8FOH5luchRibu
			nGnu9/t6RsIycBJgKEKClYKallv0SrLiavktijmIIGRdmGd6h0JaC2ld6BQQPIn5rjyDtbUGarUV
			1IlarY5Wq4P5W7M4e/oEavVVCtgsavJF+UKlms+PlwxVO50NemdKJEgp0wKOzGNtnPckMRgf3wOP
			hY4KPgVmnlajaKzkSEWK+Ck9s6yJmbn0nLlO9VyxuUbFgALnF6u5XLE0tdXC3Oo7RORphCxmCL0d
			6V4OWkZY2juBD4GHYjGkwLnH1TAslA6iibl9lqEGJLI58IjRSgifIh7TCrDcMni90mYqOfiWOTMx
			6APT6N+YRRBsT4JDNcTIpCeRX/4M8+I97Bjn8E8buboi7Qx8cA7cPDDGrI02FWB7/gfORMpNZycV
			GA6Fa5873AH4k2RROwFj9KbVCeMaRZCF+G/QKVOIk/iXZPkedq36mFjTSGJFJMP17+CIN3pWfW/2
			end/A6zS9N1pdGYnAAAAAElFTkSuQmCC
			"""; // $NON-NLS-1$
	public static final String USER = """
			iVBORw0KGgoAAAANSUhEUgAAAA8AAAAQCAYAAADJViUEAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAIDSURBVDhPXVO/Sx1BEJ6dvX3PIkVUbAxCsBJT
			pNGnIEYJCoE0IUUQBP+AECEREawUBU3jf2FSBARtLO1E8kAbSYgWIRYSVJLiQUDvbm/9Znd95t48
			5mb27vtmdn48RS0y0f+uR5Fa1toMajZasy6U0qcArm0frR5GmJcSGUQDs6sUTxhdIaPbqK36gExS
			IWvzX7aww1++Ll4ENBFHeye90BFWTMhGipnEZ/jQx8hUC7AgrWQ5Q3FxBcUPGSnLU3LygU3iUVFK
			5CxLx2D+A4QgITNTbm+evRlab35vkms90+036fWSc047yeMkFzxYf4Y2/v2ZRbgnngBpktP8usHM
			O3AL5wo8RG3QIic0iyqmuodAZ4HR0u3J/tmH+PgNr7sTbdDtKiXoOkYm105RwwC6fRzh5ZpBbMCc
			iFcgk/UZM8qLVPS3tdl5QAbR0Xr5eVV3vV2173DHEagzvPVyidrfbx+tlJaklDkKNorr6LCFja/o
			kFlfvHy6UMI3a8Z2dcB8BGFGc1LVKsFcE9SLMYmqxMLfQf1znw/mfdM8GcQumC0QR+9JZb0LBAx6
			Qq829z/8UCDKVT5Bp/wylEjxHANilMjG0ti6K+ykRoNegLgGlW30D9kq8UXkDDCWJcxeJoC5P4L9
			q573vd0A9jV2yAY8/gj+DyF1+mv61YQT48nTKefc8S3H2rg6S3coSQAAAABJRU5ErkJggg==
			"""; // $NON-NLS-1$
	public static final String WARN = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAOCAYAAAAmL5yKAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAHgSURBVDhPfVJNaBNBGH07O5vNZjeJbf25NAVb
			sBLRo0ShhypIsAqWHgpFaaEITU8VBBGlWCpevHkT0avgTfDkoRfx4MmTeJBWMEilpSRNE5tmdnf6
			Tbq7sl31wWO+3/fNN7v4Hzbf35h3v918IuVjLQglwIIzge+vx07ZWb6se/49rK9dDsIJ/FMg15t6
			aKV5L9o+Q1MsyVYlHaRi+KtA9c31Ucfhk42GxNaWBH67F7G5eytIx5AQ+Lg8amZzxlKKs9S7Dy5e
			vBXwd31gR9yXG7dPBGUREgKDp7NTtsVH4AF7e0CbKF1KNL2TqHfuHFT9QUzg6/PyMcc2HnCNHp0E
			NBoM2kDZEGRsi4qszp5VtSFiAn095kLG1Ie6DWqqalYiyldseTnUxaKUz6LPGgmsvbxadCxjnknK
			qWZqkOENlK8oiHUxjtXPZbK6iATyTmoxzfUjYbMip4OTnhbeQLHl66iJR7IxlyHvQODHq2tXHJNP
			dHcOC0mofI5hekQHC98hZM09j2p7hixon55esob78yv5jFFSgQg0eeWLh41tYPKCrtw4eowqhqwS
			K/TZ07bBS7EJAQt5huHjWnyFkDW3gF+du8wUetFtSrR3/DgbPgYciTNH6V84nFOseRA/O8V9imDH
			WRpjUvsAAAAASUVORK5CYII=
			"""; // $NON-NLS-1$
	public static final String WEB = """
			iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
			jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAANZSURBVDhPZVNraFxFFD7nzNy7e+9ms2a3rY9o
			A402EKriD60ttYZGsQ/TCD5aK1EoRRSsUEXBB4pCKxb9IYg/1AoqQoI1qWCl1CjEgtFQbH/YYrAs
			jdQmTdPtJrF37507M8e5oRTUAwfOzHzfmTPnfIPwH9v1ly2nCu63FtYBQxsJQN+DM34eRoKQv349
			FNOXoQv2rwQ7TtptKoE3EwVsNbczIyIBCMEQ5OFUsQReUzPsuaGFP34apc04VxL0HTWvNRQ92Yjh
			5VI+XgGC1isjJ9xRwRO6AL48rubtcGUx7W1ZBIPXLIIXd6EwLj/Ao0eSJxoJvoECvq+UzbWyIJ9N
			hBzzQvS8AuaMxV884u1hRXbW5+DQzDl+brqGOzMubhm6cLUqNv/MHk96Uqe5QK42lsd1aqakLzuQ
			0Evj9IQUVBYe3ZJEejSNiVvbvPbyElhDiREPG6TaxKTpqp9R26MEzp+elY98uSq857yW/TNaHHJx
			99lq0vu3wqnJNLfzQPc7a+dm+fdGhH1kGNe7Xg0d2xqooFneahDPjXaLk1l5yoLNPItHtpb+ZIY/
			Ap/uAHiVo3keiCO+F3sG6sdybaXxXAHGbGLWsaTlicV9hsESQQ8iFIyBfgGMebKPGRQ1T8A38Zzt
			rJThbmJtyGq9XEfxWrZmGVpzFalkjVTxXb7VrT7rJVks3B5aW0Gtl2ZYx+l0iQVxrGeSCD8bWBk8
			EF3Ub0UKql/dme8ZXBVsnlJicFqJ4Sze7/ySwt8uKvGBw/aqRLzvZlAjjtSPmKQbs3fqS/qoK/X6
			24b5umztC7AeOT066+qfLwmBHa5nPy2c5XCzU+kobni72iFaSyPBTeUBP2dTh39KEx10DRt3MrvP
			9SBkCweEsUvRoy1O4h+mDVDE8LgT1CZxavi9Czd2PVOCnHwBPFKC7AlXxUOqoY0UUHZKC3SU5oUH
			DwLzYR3bxSbFviDATz9ZQR8tKBG03ZNO1PdHZ1WxNuvvZmW+cKVHWuOoVvCrBK4B0bf1unwljr1A
			Svwun4eXMuqVv7Dx3dOhe/RurDRto5bweL5IXSQIM4TTCcUN/MFN4WZJfDAM7POfr/RnM97/vvOG
			vdXbsSm3w/lqCrx2Nyhyt1cReEwg7xvqLR65DHUG8A+NCo1e7bjm6gAAAABJRU5ErkJggg==
			"""; // $NON-NLS-1$

	/**
	 * Gets the image.
	 *
	 * @param display the display
	 * @param image   the image
	 * @return the image
	 */
	public static Image getImage(final Display display, final String image) {
		final var img = new Image(display, new ByteArrayInputStream(Base64.getMimeDecoder().decode(image)));
		img.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		return img;
	}

	/**
	 * Instantiates a new img.
	 */
	private IMG() {
	}
}
