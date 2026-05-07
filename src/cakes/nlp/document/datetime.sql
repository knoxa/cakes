insert into ANNOTATIONS (DOC_ID, "BEGIN", "END", TYPE, SURFACE, LEMMA)
select D.DOC_ID, E."BEGIN", E."END", 'DATE', substr(D.CONTENT, E."BEGIN"+1, E."END"-E."BEGIN"), A.VALUE
from ELEMENTS E, ATTRIBUTES A, DOC_TEXT D where E.NAME = 'datetime' and A.NAME = 'content'
and E.DOC_ID = A.DOC_ID and E.NODE_ID = A.NODE_ID and E.DOC_ID = D.DOC_ID
;
