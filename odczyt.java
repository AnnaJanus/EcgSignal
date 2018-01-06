if (st.toString() == "Elapsed") {
					System.out.println(st.toString());
					st.nextToken();
				} else if (st.toString() == "time") {
					System.out.println(st.toString());
					st.nextToken();
				} else if (st.toString() == "EKG") {
					System.out.println(st.toString());
					st.nextToken();
				} else if (st.toString() == "I") {
					System.out.println(st.toString());
					st.nextToken();
				} else if (st.toString() == "(seconds)") {
					System.out.println(st.toString());
					Tunit = "s";
					System.out.println(Tunit);
					st.nextToken();
				} else if (st.toString() == "(mV)") {
					System.out.println(st.toString());
					Vunit = "mV";
					System.out.println(Vunit);
					st.nextToken();
				} else