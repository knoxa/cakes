insert into GRAPH (SUBJECT, STYPE, PREDICATE, OBJECT, OTYPE)
select distinct lower(A.SURFACE), '0', 'name', lower(B.SURFACE), '1' from ANNOTATIONS A, ANNOTATIONS B
where A.DOC_ID = B.DOC_ID and A.TYPE = '*person' and B.TYPE = '*name-person'
and B."BEGIN" between A."BEGIN" and A."END" and B."END" between A."BEGIN" and A."END";

insert into GRAPH (SUBJECT, STYPE, PREDICATE, OBJECT, OTYPE)
select distinct lower(A.SURFACE), '1', 'token', lower(B.SURFACE), '2' from ANNOTATIONS A, PARSED_TEXT B
where A.DOC_ID = B.DOC_ID and A.TYPE = '*name-person' and B.POS <> 'Punctuation'
and B."BEGIN" between A."BEGIN" and A."END" and B."END" between A."BEGIN" and A."END";

insert into GRAPH (SUBJECT, STYPE, PREDICATE, OBJECT, OTYPE)
select distinct lower(A.SURFACE), '0', 'name', lower(A.SURFACE), '1' from ANNOTATIONS A
where A.TYPE = '*name-person' and not exists (
	select * from ANNOTATIONS B where B.DOC_ID = A.DOC_ID and B.TYPE = '*person'and A."BEGIN" between B."BEGIN" and B."END"
);

delete from GRAPH where OTYPE = '2' and OBJECT in ('el', 'the', 'del', 'de', 'of', 'la');