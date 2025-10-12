delete from ANNOTATIONS as A where exists (

	select * from ANNOTATIONS as B
	where A.DOC_ID = B.DOC_ID
	and A.TYPE = B.TYPE and B.TYPE in ('*person', '*place', '*name-person', '*name-place', '*role')
	and A."BEGIN" between B."BEGIN" and B."END" and A."END" between B."BEGIN" and B."END"
	and (A."BEGIN" <> B."BEGIN" OR A."END" <> B."END")
);
